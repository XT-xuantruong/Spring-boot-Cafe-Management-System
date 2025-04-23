/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from "@/components/ui/table";
import { 
  Button 
} from "@/components/ui/button";
import { 
  Dialog, 
  DialogContent, 
  DialogHeader, 
  DialogTitle, 
  DialogTrigger 
} from "@/components/ui/dialog";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";
import { 
  Input 
} from "@/components/ui/input";
import { 
  useGetAllPaymentsQuery, 
  useProcessPaymentMutation, 
  useUpdatePaymentStatusMutation, 
  useDeletePaymentMutation 
} from "@/services/paymentServices";
import { Payment, PaymentRequest } from "@/interfaces/payment";
import { PaymentMethod } from "@/enums/paymentMethod";
import { format } from "date-fns";
import { 
  Loader2, 
  Eye, 
  Trash2, 
  Plus 
} from "lucide-react";
import { toast } from '@/hooks/use-toast';
import { v4 as uuidv4 } from "uuid";
import { PaymentStatus } from '@/enums/paymentStatus';

const PaymentManagementPage: React.FC = () => {
  const { data: paymentsResponse, isLoading, error } = useGetAllPaymentsQuery();
  const [processPayment, { isLoading: isProcessingPayment }] = useProcessPaymentMutation();
  const [updatePaymentStatus] = useUpdatePaymentStatusMutation();
  const [deletePayment] = useDeletePaymentMutation();
  const [openDetailsDialog, setOpenDetailsDialog] = useState<number | null>(null);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [newPayment, setNewPayment] = useState<PaymentRequest>({
    orderId: 0,
    amount: 0,
    paymentMethod: PaymentMethod.CASH,
    transactionId: undefined,
  });

  const handleUpdatePaymentStatus = async (paymentId: number, status: PaymentStatus) => {
    try {
      await updatePaymentStatus({ id: paymentId, paymentStatus: status }).unwrap();
      toast({
        title: "Success",
        description: `Updated payment #${paymentId} status to ${status}`,
      });
    } catch (err) {
      toast({
        title: "Error",
        description: "Failed to update payment status",
        variant: "destructive",
      });
      console.error("Failed to update payment status:", err);
    }
  };

  const handleDeletePayment = async (paymentId: number) => {
    try {
      await deletePayment(paymentId).unwrap();
      toast({
        title: "Success",
        description: `Deleted payment #${paymentId}`,
      });
    } catch (err) {
      toast({
        title: "Error",
        description: "Failed to delete payment",
        variant: "destructive",
      });
      console.error("Failed to delete payment:", err);
    }
  };

  const validatePaymentForm = () => {
    if (newPayment.orderId <= 0) {
      return "Please enter a valid Order ID";
    }
    if (newPayment.amount <= 0) {
      return "Please enter a valid amount";
    }
    if (!newPayment.paymentMethod) {
      return "Please select a payment method";
    }
    if (newPayment.paymentMethod === PaymentMethod.ONLINE && !newPayment.transactionId) {
      return "Transaction ID is required for ONLINE payment";
    }
    return null;
  };

  const handleCreatePayment = async () => {
    const errorMessage = validatePaymentForm();
    if (errorMessage) {
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
      return;
    }

    try {
      const paymentResponse = await processPayment(newPayment).unwrap();
      toast({
        title: "Success",
        description: `Created payment #${paymentResponse.data.paymentId}`,
      });
      setOpenCreateDialog(false);
      setNewPayment({
        orderId: 0,
        amount: 0,
        paymentMethod: PaymentMethod.CASH,
        transactionId: undefined,
      });
    } catch (err: any) {
      const errorMessage = err.message || err.data?.message || "Failed to create payment";
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
      console.error("Failed to create payment:", err);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (error || !paymentsResponse) {
    return (
      <div className="text-center text-red-500 p-4">
        Error loading payments. Please try again later.
      </div>
    );
  }

  const payments: Payment[] = paymentsResponse.data;

  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Payment Management</h1>
        <Dialog open={openCreateDialog} onOpenChange={setOpenCreateDialog}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="h-4 w-4 mr-1" /> Create Payment
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-lg">
            <DialogHeader>
              <DialogTitle>Create New Payment</DialogTitle>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div>
                <label className="text-sm font-medium">Order ID</label>
                <Input
                  type="number"
                  placeholder="Enter Order ID"
                  value={newPayment.orderId}
                  onChange={(e) => setNewPayment({ ...newPayment, orderId: parseInt(e.target.value) || 0 })}
                  min="1"
                />
              </div>
              <div>
                <label className="text-sm font-medium">Amount</label>
                <Input
                  type="number"
                  placeholder="Enter Amount"
                  value={newPayment.amount}
                  onChange={(e) => setNewPayment({ ...newPayment, amount: parseFloat(e.target.value) || 0 })}
                  min="0.01"
                  step="0.01"
                />
              </div>
              <div>
                <label className="text-sm font-medium">Payment Method</label>
                <Select
                  value={newPayment.paymentMethod}
                  onValueChange={(value) => {
                    const transactionId = value === PaymentMethod.ONLINE ? uuidv4() : undefined;
                    setNewPayment({ ...newPayment, paymentMethod: value as PaymentMethod, transactionId });
                  }}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select payment method" />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(PaymentMethod).map((method) => (
                      <SelectItem key={method} value={method}>
                        {method}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              {newPayment.paymentMethod === PaymentMethod.ONLINE && (
                <div>
                  <label className="text-sm font-medium">Transaction ID</label>
                  <Input
                    type="text"
                    placeholder="Transaction ID"
                    value={newPayment.transactionId || ""}
                    disabled
                  />
                </div>
              )}
            </div>
            <div className="flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => setOpenCreateDialog(false)}
                disabled={isProcessingPayment}
              >
                Cancel
              </Button>
              <Button
                onClick={handleCreatePayment}
                disabled={isProcessingPayment || !!validatePaymentForm()}
              >
                {isProcessingPayment ? (
                  <Loader2 className="h-4 w-4 animate-spin mr-2" />
                ) : null}
                Create Payment
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Payment ID</TableHead>
              <TableHead>Order ID</TableHead>
              <TableHead>Amount</TableHead>
              <TableHead>Payment Method</TableHead>
              <TableHead>Payment Status</TableHead>
              <TableHead>Payment Time</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {payments.map((payment: Payment) => (
              <TableRow key={payment.paymentId}>
                <TableCell>{payment.paymentId}</TableCell>
                <TableCell>{payment.orderId}</TableCell>
                <TableCell>${payment.amount.toFixed(2)}</TableCell>
                <TableCell>{payment.paymentMethod}</TableCell>
                <TableCell>
                  <Select
                    value={payment.paymentStatus}
                    onValueChange={(value) =>
                      handleUpdatePaymentStatus(payment.paymentId, value as PaymentStatus)
                    }
                  >
                    <SelectTrigger className="w-[120px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.values(PaymentStatus).map((status) => (
                        <SelectItem key={status} value={status}>
                          {status}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </TableCell>
                <TableCell>
                  {payment.paymentTime
                    ? format(new Date(payment.paymentTime), "PPp")
                    : "N/A"}
                </TableCell>
                <TableCell>
                  <div className="flex space-x-2">
                    <Dialog
                      open={openDetailsDialog === payment.paymentId}
                      onOpenChange={(open) =>
                        setOpenDetailsDialog(open ? payment.paymentId : null)
                      }
                    >
                      <DialogTrigger asChild>
                        <Button variant="outline" size="sm">
                          <Eye className="h-4 w-4 mr-1" /> View
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="max-w-lg">
                        <DialogHeader>
                          <DialogTitle>Payment Details - #{payment.paymentId}</DialogTitle>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                          <p><strong>Payment ID:</strong> {payment.paymentId}</p>
                          <p><strong>Order ID:</strong> {payment.orderId}</p>
                          <p><strong>Amount:</strong> ${payment.amount.toFixed(2)}</p>
                          <p><strong>Payment Method:</strong> {payment.paymentMethod}</p>
                          <p><strong>Payment Status:</strong> {payment.paymentStatus}</p>
                          {payment.paymentTime && (
                            <p><strong>Payment Time:</strong> {format(new Date(payment.paymentTime), "PPp")}</p>
                          )}
                          {payment.transactionId && (
                            <p><strong>Transaction ID:</strong> {payment.transactionId}</p>
                          )}
                          <p><strong>Created:</strong> {format(new Date(payment.createdAt), "PPp")}</p>
                          <p><strong>Updated:</strong> {format(new Date(payment.updatedAt), "PPp")}</p>
                        </div>
                      </DialogContent>
                    </Dialog>
                    <Button
                      disabled={true}
                      variant="destructive"
                      size="sm"
                      onClick={() => handleDeletePayment(payment.paymentId)}
                    >
                      <Trash2 className="h-4 w-4 mr-1" /> Delete
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};

export default PaymentManagementPage;