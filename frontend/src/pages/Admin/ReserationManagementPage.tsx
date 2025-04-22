import { useState } from "react";
import { format } from "date-fns";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "@/hooks/use-toast";
import { Loader2, Trash2, Edit, Plus } from "lucide-react";
import {
  useGetAllReservationsQuery,
  useGetAvailableTablesQuery,
  useCreateReservationMutation,
  useUpdateReservationStatusMutation,
  useDeleteReservationMutation,
} from "@/services/reservationServices";
import { Reservation } from "@/interfaces/reservation";
import { CafeTable } from "@/interfaces/cafetable";
import { ReservationStatus } from "@/interfaces/reservation";

const ReservationManagementPage = () => {
  const { data: reservationsData, error: reservationsError, isLoading: isReservationsLoading } =
    useGetAllReservationsQuery();
  const { data: tablesData, error: tablesError, isLoading: isTablesLoading } =
    useGetAvailableTablesQuery();
  const [createReservation, { isLoading: isCreating }] = useCreateReservationMutation();
  const [updateReservationStatus, { isLoading: isUpdating }] =
    useUpdateReservationStatusMutation();
  const [deleteReservation, { isLoading: isDeleting }] = useDeleteReservationMutation();

  const [selectedReservation, setSelectedReservation] = useState<Reservation | null>(null);
  const [newStatus, setNewStatus] = useState<ReservationStatus | "">("");
  const [isUpdateDialogOpen, setIsUpdateDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isOrderDialogOpen, setIsOrderDialogOpen] = useState(false);
  const [createForm, setCreateForm] = useState({
    tableId: "",
    reservationTime: "",
  });

  const handleCreate = async () => {
    if (!createForm.tableId || !createForm.reservationTime) {
      toast({
        title: "Error",
        description: "Please fill in all fields",
        variant: "destructive",
      });
      return;
    }

    try {
      const result = await createReservation({
        tableId: parseInt(createForm.tableId),
        reservationTime: createForm.reservationTime,
      }).unwrap();
      toast({
        title: "Success",
        description: `Created reservation #${result.data.reservationId}`,
      });
      setIsCreateDialogOpen(false);
      setCreateForm({ tableId: "", reservationTime: "" });
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to create reservation",
        variant: "destructive",
      });
      console.error(error);
    }
  };

  const handleUpdateStatus = async () => {
    if (!selectedReservation || !newStatus) return;

    try {
      const result = await updateReservationStatus({
        id: selectedReservation.reservationId.toString(),
        status: newStatus,
      }).unwrap();
      toast({
        title: "Success",
        description: `Updated reservation #${result.data.reservationId} to ${newStatus}`,
      });
      setIsUpdateDialogOpen(false);
      setNewStatus("");
      setSelectedReservation(null);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to update reservation status",
        variant: "destructive",
      });
      console.error(error);
    }
  };

  const handleDelete = async () => {
    if (!selectedReservation) return;

    try {
      await deleteReservation(selectedReservation.reservationId.toString()).unwrap();
      toast({
        title: "Success",
        description: `Deleted reservation #${selectedReservation.reservationId}`,
      });
      setIsDeleteDialogOpen(false);
      setSelectedReservation(null);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to delete reservation",
        variant: "destructive",
      });
      console.error(error);
    }
  };

  const openUpdateDialog = (reservation: Reservation) => {
    setSelectedReservation(reservation);
    setNewStatus(reservation.status);
    setIsUpdateDialogOpen(true);
  };

  const openDeleteDialog = (reservation: Reservation) => {
    setSelectedReservation(reservation);
    setIsDeleteDialogOpen(true);
  };

  const openOrderDialog = (reservation: Reservation) => {
    setSelectedReservation(reservation);
    setIsOrderDialogOpen(true);
  };

  if (isReservationsLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <Loader2 className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  if (reservationsError) {
    return (
      <div className="text-center text-red-500">
        Error: {JSON.stringify(reservationsError)}
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">Reservation Management</h1>

      <div className="mb-4">
        <Button onClick={() => setIsCreateDialogOpen(true)}>
          <Plus className="w-4 h-4 mr-2" /> Create Reservation
        </Button>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>Customer</TableHead>
            <TableHead>Table</TableHead>
            <TableHead>Time</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Order</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {reservationsData?.data.map((reservation: Reservation) => (
            <TableRow key={reservation.reservationId}>
              <TableCell>{reservation.reservationId}</TableCell>
              <TableCell>{reservation.user.name}</TableCell>
              <TableCell>{reservation.cafeTable.tableNumber}</TableCell>
              <TableCell>
                {format(new Date(reservation.reservationTime), "MMM dd, yyyy HH:mm")}
              </TableCell>
              <TableCell>
                {reservation.status === ReservationStatus.PENDING && (
                    <span className="inline-block px-2 py-1 rounded bg-yellow-100 text-yellow-800">
                    {reservation.status}
                    </span>
                )}
                {reservation.status === ReservationStatus.CONFIRMED && (
                    <span className="inline-block px-2 py-1 rounded bg-green-100 text-green-800">
                    {reservation.status}
                    </span>
                )}
                {reservation.status === ReservationStatus.CANCELLED && (
                    <span className="inline-block px-2 py-1 rounded bg-red-100 text-red-800">
                    {reservation.status}
                    </span>
                )}
              </TableCell>
              <TableCell>
                {reservation.order ? (
                  <Button
                    variant="link"
                    onClick={() => openOrderDialog(reservation)}
                    className="p-0 h-auto"
                  >
                    Order #{reservation.order.orderId} (${reservation.order.totalAmount})
                  </Button>
                ) : (
                  "No order"
                )}
              </TableCell>
              <TableCell>
                <div className="flex space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => openUpdateDialog(reservation)}
                    disabled={isUpdating}
                  >
                    <Edit className="w-4 h-4 mr-1" /> Update
                  </Button>
                  <Button
                    variant="destructive"
                    size="sm"
                    onClick={() => openDeleteDialog(reservation)}
                    disabled={isDeleting}
                  >
                    <Trash2 className="w-4 h-4 mr-1" /> Delete
                  </Button>
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Dialog for creating reservation */}
      <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create New Reservation</DialogTitle>
            <DialogDescription>Enter details to create a new reservation.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="tableId" className="text-right">
                Table
              </Label>
              <Select
                value={createForm.tableId}
                onValueChange={(value) => setCreateForm({ ...createForm, tableId: value })}
              >
                <SelectTrigger className="col-span-3">
                  <SelectValue placeholder="Select a table" />
                </SelectTrigger>
                <SelectContent>
                  {isTablesLoading ? (
                    <SelectItem value="" disabled>
                      Loading tables...
                    </SelectItem>
                  ) : tablesError ? (
                    <SelectItem value="" disabled>
                      Error loading tables
                    </SelectItem>
                  ) : (
                    tablesData?.data.map((table: CafeTable) => (
                      <SelectItem
                        key={table.tableId}
                        value={table.tableId.toString()}
                      >
                        {table.tableNumber} (Capacity: {table.capacity})
                      </SelectItem>
                    ))
                  )}
                </SelectContent>
              </Select>
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="reservationTime" className="text-right">
                Reservation Time
              </Label>
              <Input
                id="reservationTime"
                type="datetime-local"
                value={createForm.reservationTime}
                onChange={(e) =>
                  setCreateForm({ ...createForm, reservationTime: e.target.value })
                }
                className="col-span-3"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleCreate} disabled={isCreating}>
              {isCreating ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Create
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog for updating status */}
      <Dialog open={isUpdateDialogOpen} onOpenChange={setIsUpdateDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Update Reservation Status</DialogTitle>
            <DialogDescription>
              Change the status for reservation #{selectedReservation?.reservationId}.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Select
              value={newStatus}
              onValueChange={(value) => setNewStatus(value as ReservationStatus)}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={ReservationStatus.PENDING}>
                  <span className="inline-block px-2 py-1 rounded bg-yellow-100 text-yellow-800">
                    Pending
                  </span>
                </SelectItem>
                <SelectItem value={ReservationStatus.CONFIRMED}>
                  <span className="inline-block px-2 py-1 rounded bg-green-100 text-green-800">
                    Confirmed
                  </span>
                </SelectItem>
                <SelectItem value={ReservationStatus.CANCELLED}>
                  <span className="inline-block px-2 py-1 rounded bg-red-100 text-red-800">
                    Cancelled
                  </span>
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsUpdateDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleUpdateStatus} disabled={isUpdating || !newStatus}>
              {isUpdating ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Save
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog for deleting reservation */}
      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Reservation</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete reservation #{selectedReservation?.reservationId}?
              This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleDelete} disabled={isDeleting}>
              {isDeleting ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog for viewing order details */}
      <Dialog open={isOrderDialogOpen} onOpenChange={setIsOrderDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Order Details</DialogTitle>
            <DialogDescription>
              Details for order #{selectedReservation?.order?.orderId}
            </DialogDescription>
          </DialogHeader>
          {selectedReservation?.order && (
            <div className="py-4 space-y-2">
              <p>
                <strong>Order ID:</strong> {selectedReservation.order.orderId}
              </p>
              <p>
                <strong>Total Amount:</strong> ${selectedReservation.order.totalAmount}
              </p>
              <p>
                <strong>Order Status:</strong> {selectedReservation.order.orderStatus}
              </p>
              <p>
                <strong>Payment Status:</strong> {selectedReservation.order.paymentStatus}
              </p>
            </div>
          )}
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsOrderDialogOpen(false)}>
              Close
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ReservationManagementPage;