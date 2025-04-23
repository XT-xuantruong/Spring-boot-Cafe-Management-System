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
  Badge 
} from "@/components/ui/badge";
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
  useGetAllOrdersQuery, 
  useUpdateOrderStatusMutation, 
  useCancelOrderMutation, 
  useCreateOrderMutation 
} from "@/services/orderServices";
import { useProcessPaymentMutation } from "@/services/paymentServices";
import { Order, OrderItem, OrderRequest } from "@/interfaces/order";
import { OrderStatus } from '@/enums/orderStatus';
import { format } from "date-fns";
import { 
  Loader2, 
  Eye, 
  Trash2, 
  Plus 
} from "lucide-react";
import { useSelector } from 'react-redux';
import { RootState } from '@/stores';
import { useGetAllReservationsQuery, useGetAvailableTablesQuery } from '@/services/reservationServices';
import { useGetAllMenuItemsQuery } from '@/services/menuItemServices';
import { toast } from '@/hooks/use-toast';
import { v4 as uuidv4 } from "uuid";
import { PaymentMethod } from '../../enums/paymentMethod';

const OrderManagementPage: React.FC = () => {
  const userId = useSelector((state: RootState) => state.auth.user?.id);
  const { data: ordersResponse, isLoading, error } = useGetAllOrdersQuery();
  const { data: cafeTable, isLoading: isCafeTableLoading } = useGetAvailableTablesQuery();
  const { data: reservation, isLoading: isReservationLoading } = useGetAllReservationsQuery();
  const { data: menuItemsResponse, isLoading: isMenuItemsLoading } = useGetAllMenuItemsQuery();
  const [updateOrderStatus] = useUpdateOrderStatusMutation();
  const [cancelOrder] = useCancelOrderMutation();
  const [createOrder, { isLoading: isCreatingOrder }] = useCreateOrderMutation();
  const [processPayment, { isLoading: isProcessingPayment }] = useProcessPaymentMutation();
  const [openDetailsDialog, setOpenDetailsDialog] = useState<string | null>(null);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [newOrder, setNewOrder] = useState<OrderRequest>({
    userId: userId || 0,
    tableId: 0,
    reservationId: undefined,
    orderStatus: OrderStatus.PENDING,
    orderItems: [{ itemId: 0, quantity: 1 }],
  });
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<PaymentMethod>(PaymentMethod.CASH);

  const menuItems = menuItemsResponse?.data || [];

  const handleStatusChange = async (orderId: string, status: OrderStatus) => {
    try {
      await updateOrderStatus({ id: orderId, status }).unwrap();
      toast({
        title: "Success",
        description: `Updated order #${orderId} status to ${status}`,
      });
    } catch (err) {
      toast({
        title: "Error",
        description: "Failed to update order status",
        variant: "destructive",
      });
      console.error("Failed to update order status:", err);
    }
  };

  const handleCancelOrder = async (orderId: string) => {
    try {
      await cancelOrder(orderId).unwrap();
      toast({
        title: "Success",
        description: `Cancelled order #${orderId}`,
      });
    } catch (err) {
      toast({
        title: "Error",
        description: "Failed to cancel order",
        variant: "destructive",
      });
      console.error("Failed to cancel order:", err);
    }
  };

  const validateOrderForm = () => {
    if (!userId || newOrder.userId === 0) {
      return "Please log in to create an order";
    }
    if (newOrder.tableId === 0) {
      return "Please select a table";
    }
    if (newOrder.orderItems.length === 0 || newOrder.orderItems.some(item => item.itemId === 0 || item.quantity <= 0)) {
      return "Please add valid order items with quantity";
    }
    if (!selectedPaymentMethod) {
      return "Please select a payment method";
    }
    return null;
  };

  const calculateTotal = () => {
    return newOrder.orderItems.reduce((total, item) => {
      const menuItem = menuItems.find(m => m.itemId === item.itemId);
      return total + (menuItem ? menuItem.price * item.quantity : 0);
    }, 0);
  };

  const handleCreateOrder = async () => {
    const errorMessage = validateOrderForm();
    if (errorMessage) {
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
      return;
    }

    try {
      // Create Order
      const orderResponse = await createOrder(newOrder).unwrap();
      const orderId = orderResponse.data.orderId;
      const totalAmount = calculateTotal();

      // Create Payment
      const paymentRequest = {
        orderId,
        amount: totalAmount,
        paymentMethod: selectedPaymentMethod,
        transactionId: selectedPaymentMethod === PaymentMethod.ONLINE ? uuidv4() : undefined,
      };
      const paymentResponse = await processPayment(paymentRequest).unwrap();

      toast({
        title: "Success",
        description: `Created order #${orderId} and payment #${paymentResponse.data.paymentId}`,
      });

      setOpenCreateDialog(false);
      setNewOrder({
        userId: userId || 0,
        tableId: 0,
        reservationId: undefined,
        orderStatus: OrderStatus.PENDING,
        orderItems: [{ itemId: 0, quantity: 1 }],
      });
      setSelectedPaymentMethod(PaymentMethod.CASH);
    } catch (err: any) {
      const errorMessage = err.message || err.data?.message || "Failed to create order or payment";
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
      console.error("Failed to create order or payment:", err);
    }
  };

  const addOrderItem = () => {
    setNewOrder({
      ...newOrder,
      orderItems: [...newOrder.orderItems, { itemId: 0, quantity: 1 }],
    });
  };

  const removeOrderItem = (index: number) => {
    setNewOrder({
      ...newOrder,
      orderItems: newOrder.orderItems.filter((_, i) => i !== index),
    });
  };

  const updateOrderItem = (index: number, field: 'itemId' | 'quantity', value: number) => {
    const updatedItems = [...newOrder.orderItems];
    updatedItems[index] = { ...updatedItems[index], [field]: value };
    setNewOrder({ ...newOrder, orderItems: updatedItems });
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (error || !ordersResponse) {
    return (
      <div className="text-center text-red-500 p-4">
        Error loading orders. Please try again later.
      </div>
    );
  }

  const orders = ordersResponse.data;

  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Order Management</h1>
        <Dialog open={openCreateDialog} onOpenChange={setOpenCreateDialog}>
          <DialogTrigger asChild>
            <Button disabled={isCafeTableLoading || isReservationLoading || isMenuItemsLoading}>
              <Plus className="h-4 w-4 mr-1" /> Create Order
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-lg">
            <DialogHeader>
              <DialogTitle>Create New Order</DialogTitle>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div>
                <label className="text-sm font-medium">Table</label>
                <Select
                  value={newOrder.tableId.toString()}
                  onValueChange={(value) => setNewOrder({ ...newOrder, tableId: parseInt(value) })}
                  disabled={isCafeTableLoading}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a table" />
                  </SelectTrigger>
                  <SelectContent>
                    {isCafeTableLoading ? (
                      <SelectItem value="0" disabled>Loading tables...</SelectItem>
                    ) : (
                      cafeTable?.data.map((item) => (
                        <SelectItem key={item.tableId} value={item.tableId.toString()}>
                          {item.tableNumber}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <label className="text-sm font-medium">Reservation (Optional)</label>
                <Select
                  value={newOrder.reservationId?.toString() || "none"}
                  onValueChange={(value) => setNewOrder({ 
                    ...newOrder, 
                    reservationId: value === "none" ? undefined : parseInt(value) 
                  })}
                  disabled={isReservationLoading}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a reservation" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="none">None</SelectItem>
                    {isReservationLoading ? (
                      <SelectItem value="0" disabled>Loading reservations...</SelectItem>
                    ) : (
                      reservation?.data.map((item) => (
                        <SelectItem key={item.reservationId} value={item.reservationId.toString()}>
                          {item.userName} - {item.tableNumber}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <label className="text-sm font-medium">Order Status</label>
                <Select
                  value={newOrder.orderStatus}
                  onValueChange={(value) => setNewOrder({ ...newOrder, orderStatus: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(OrderStatus).map((status) => (
                      <SelectItem key={status} value={status}>
                        {status}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <label className="text-sm font-medium">Payment Method</label>
                <Select
                  value={selectedPaymentMethod}
                  onValueChange={(value) => setSelectedPaymentMethod(value as PaymentMethod)}
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
              <div>
                <h3 className="font-semibold mb-2">Order Items</h3>
                {newOrder.orderItems.map((item, index) => (
                  <div key={index} className="flex gap-2 mb-2 items-center">
                    <Select
                      value={item.itemId.toString()}
                      onValueChange={(value) => updateOrderItem(index, 'itemId', parseInt(value))}
                      disabled={isMenuItemsLoading}
                    >
                      <SelectTrigger className="w-[250px]">
                        <SelectValue placeholder="Select an item" />
                      </SelectTrigger>
                      <SelectContent>
                        {isMenuItemsLoading ? (
                          <SelectItem value="0" disabled>Loading menu items...</SelectItem>
                        ) : (
                          menuItems.map((menuItem) => (
                            <SelectItem key={menuItem.itemId} value={menuItem.itemId.toString()}>
                              <div className="flex items-center gap-2">
                                {menuItem.imageUrl ? (
                                  <img
                                    src={menuItem.imageUrl}
                                    alt={menuItem.itemName}
                                    className="w-6 h-6 object-cover rounded"
                                  />
                                ) : (
                                  <span className="text-xs text-gray-500">No Image</span>
                                )}
                                <span>{menuItem.itemName} (${menuItem.price.toFixed(2)})</span>
                              </div>
                            </SelectItem>
                          ))
                        )}
                      </SelectContent>
                    </Select>
                    <Input
                      type="number"
                      placeholder="Quantity"
                      value={item.quantity}
                      onChange={(e) => updateOrderItem(index, 'quantity', parseInt(e.target.value) || 1)}
                      className="w-[100px]"
                      min="1"
                      disabled={isMenuItemsLoading}
                    />
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={() => removeOrderItem(index)}
                      disabled={newOrder.orderItems.length === 1 || isMenuItemsLoading}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                ))}
                <Button 
                  variant="outline" 
                  onClick={addOrderItem}
                  disabled={isMenuItemsLoading}
                >
                  Add Item
                </Button>
              </div>
              <div>
                <p className="text-sm font-medium">
                  Total: ${calculateTotal().toFixed(2)}
                </p>
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => setOpenCreateDialog(false)}
                disabled={isCreatingOrder || isProcessingPayment}
              >
                Cancel
              </Button>
              <Button
                onClick={handleCreateOrder}
                disabled={isCreatingOrder || isProcessingPayment || !!validateOrderForm()}
              >
                {(isCreatingOrder || isProcessingPayment) ? (
                  <Loader2 className="h-4 w-4 animate-spin mr-2" />
                ) : null}
                Create Order
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Order ID</TableHead>
              <TableHead>User</TableHead>
              <TableHead>Table</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Total Amount</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {orders.map((order: Order) => (
              <TableRow key={order.orderId}>
                <TableCell>{order.orderId}</TableCell>
                <TableCell>{order.userName}</TableCell>
                <TableCell>{order.tableNumber}</TableCell>
                <TableCell>
                  <Select
                    value={order.orderStatus}
                    onValueChange={(value) => 
                      handleStatusChange(order.orderId.toString(), value as OrderStatus)
                    }
                  >
                    <SelectTrigger className="w-[120px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.values(OrderStatus).map((status) => (
                        <SelectItem key={status} value={status}>
                          <Badge variant={
                            status === OrderStatus.PENDING ? "default" :
                            status === OrderStatus.PREPARING ? "secondary" :
                            status === OrderStatus.SERVED ? "outline" :
                            status === OrderStatus.COMPLETED ? "default" :
                            "destructive"
                          }>
                            {status}
                          </Badge>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </TableCell>
                <TableCell>${order.totalAmount.toFixed(2)}</TableCell>
                <TableCell>
                  <div className="flex space-x-2">
                    <Dialog
                      open={openDetailsDialog === order.orderId.toString()}
                      onOpenChange={(open) => 
                        setOpenDetailsDialog(open ? order.orderId.toString() : null)
                      }
                    >
                      <DialogTrigger asChild>
                        <Button variant="outline" size="sm">
                          <Eye className="h-4 w-4 mr-1" /> View
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="max-w-3xl">
                        <DialogHeader>
                          <DialogTitle>Order Details - #{order.orderId}</DialogTitle>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                          <div>
                            <h3 className="font-semibold">Order Information</h3>
                            <p>User: {order.userName}</p>
                            <p>Table: {order.tableNumber}</p>
                            <p>Status: {order.orderStatus}</p>
                            <p>Created: {format(new Date(order.createdAt), "PPp")}</p>
                            <p>Total: ${order.totalAmount.toFixed(2)}</p>
                          </div>
                          <div>
                            <h3 className="font-semibold">Order Items</h3>
                            <Table>
                              <TableHeader>
                                <TableRow>
                                  <TableHead>Image</TableHead>
                                  <TableHead>Item</TableHead>
                                  <TableHead>Quantity</TableHead>
                                  <TableHead>Unit Price</TableHead>
                                  <TableHead>Subtotal</TableHead>
                                </TableRow>
                              </TableHeader>
                              <TableBody>
                                {order.orderItems.map((item: OrderItem) => (
                                  <TableRow key={item.orderItemId}>
                                    <TableCell>
                                      {item.imageUrl ? (
                                        <img
                                          src={item.imageUrl}
                                          alt={item.itemName}
                                          className="w-16 h-16 object-cover rounded"
                                        />
                                      ) : (
                                        <span>No Image</span>
                                      )}
                                    </TableCell>
                                    <TableCell>{item.itemName}</TableCell>
                                    <TableCell>{item.quantity}</TableCell>
                                    <TableCell>${item.unitPrice.toFixed(2)}</TableCell>
                                    <TableCell>${item.subtotal.toFixed(2)}</TableCell>
                                  </TableRow>
                                ))}
                              </TableBody>
                            </Table>
                          </div>
                          {order.payment && (
                            <div>
                              <h3 className="font-semibold">Payment Information</h3>
                              <p>Amount: ${order.payment.amount.toFixed(2)}</p>
                              <p>Payment Method: {order.payment.paymentMethod}</p>
                              <p>Payment Status: {order.payment.paymentStatus}</p>
                              {order.payment.paymentTime && (
                                <p>Payment Time: {format(new Date(order.payment.paymentTime), "PPp")}</p>
                              )}
                              {order.payment.transactionId && (
                                <p>Transaction ID: {order.payment.transactionId}</p>
                              )}
                            </div>
                          )}
                        </div>
                      </DialogContent>
                    </Dialog>
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={() => handleCancelOrder(order.orderId.toString())}
                      disabled={order.orderStatus === OrderStatus.CANCELLED}
                    >
                      <Trash2 className="h-4 w-4 mr-1" /> Cancel
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

export default OrderManagementPage;