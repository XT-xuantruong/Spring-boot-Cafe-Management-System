import { useState } from "react";
import { useSelector } from "react-redux";
import { format, setHours, setMinutes } from "date-fns";
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
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import { Label } from "@/components/ui/label";
import { toast } from "@/hooks/use-toast";
import { Loader2, Trash2, Edit, Plus, CalendarIcon } from "lucide-react";
import {
  useGetAllReservationsQuery,
  useGetAvailableTablesQuery,
  useCreateReservationMutation,
  useUpdateReservationStatusMutation,
  useDeleteReservationMutation,
} from "@/services/reservationServices";
import { Reservation } from "@/interfaces/reservation";
import { CafeTable } from "@/interfaces/cafetable";
import { RootState } from "@/stores";
import { ReservationStatus } from "@/enums/reservationStatus";
import { cn } from "@/lib/utils";

const ReservationManagementPage = () => {
  const { data: reservationsData, error: reservationsError, isLoading: isReservationsLoading } =
    useGetAllReservationsQuery();
  const { data: tablesData, error: tablesError, isLoading: isTablesLoading } =
    useGetAvailableTablesQuery();
  const [createReservation, { isLoading: isCreating }] = useCreateReservationMutation();
  const [updateReservationStatus, { isLoading: isUpdating }] =
    useUpdateReservationStatusMutation();
  const [deleteReservation, { isLoading: isDeleting }] = useDeleteReservationMutation();

  const userId = useSelector((state: RootState) => state.auth.user?.id);

  const [selectedReservation, setSelectedReservation] = useState<Reservation | null>(null);
  const [newStatus, setNewStatus] = useState<ReservationStatus | "">("");
  const [isUpdateDialogOpen, setIsUpdateDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isOrderDialogOpen, setIsOrderDialogOpen] = useState(false);
  const [createForm, setCreateForm] = useState({
    tableId: "",
    date: null as Date | null,
    time: "",
    status: ReservationStatus.PENDING,
  });

  const generateTimeOptions = () => {
    const times = [];
    for (let hour = 0; hour < 24; hour++) {
      for (let minute = 0; minute < 60; minute += 30) {
        const time = `${hour.toString().padStart(2, "0")}:${minute.toString().padStart(2, "0")}`;
        times.push(time);
      }
    }
    return times;
  };

  const handleCreate = async () => {
    if (!userId) {
      toast({
        title: "Error",
        description: "Please log in to create a reservation",
        variant: "destructive",
      });
      return;
    }
    if (!createForm.tableId || !createForm.date || !createForm.time) {
      toast({
        title: "Error",
        description: "Please fill in all fields",
        variant: "destructive",
      });
      return;
    }

    const [hours, minutes] = createForm.time.split(":").map(Number);
    const reservationTime = setHours(setMinutes(createForm.date, minutes), hours);
    if (reservationTime < new Date()) {
      toast({
        title: "Error",
        description: "Reservation time must be in the present or future",
        variant: "destructive",
      });
      return;
    }

    try {
      const result = await createReservation({
        userId,
        tableId: parseInt(createForm.tableId),
        reservationTime: reservationTime.toISOString(),
        status: createForm.status,
      }).unwrap();
      toast({
        title: "Success",
        description: `Created reservation #${result.data.reservationId}`,
      });
      setIsCreateDialogOpen(false);
      setCreateForm({ tableId: "", date: null, time: "", status: ReservationStatus.PENDING });
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
        <Button
          className="bg-blue-600 hover:bg-blue-700 text-white"
          onClick={() => setIsCreateDialogOpen(true)}
        >
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
              <TableCell>{reservation.userName}</TableCell>
              <TableCell>{reservation.tableNumber}</TableCell>
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
                {reservation.orderId ? (
                  <Button
                    variant="link"
                    onClick={() => openOrderDialog(reservation)}
                    className="p-0 h-auto text-blue-600 hover:underline"
                  >
                    Order #{reservation.orderId} (${reservation.totalAmount?.toFixed(2) || "0.00"})
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
                    className="border-blue-600 text-blue-600 hover:bg-blue-50"
                  >
                    <Edit className="w-4 h-4 mr-1" /> Update
                  </Button>
                  <Button
                    variant="destructive"
                    size="sm"
                    onClick={() => openDeleteDialog(reservation)}
                    disabled={true} 
                    className="bg-red-600 hover:bg-red-700"
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
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Create New Reservation</DialogTitle>
            <DialogDescription>Enter details to create a new reservation.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="tableId" className="text-right font-medium">
                Table
              </Label>
              <Select
                value={createForm.tableId}
                onValueChange={(value) => setCreateForm({ ...createForm, tableId: value })}
              >
                <SelectTrigger className="col-span-3 border-gray-300 focus:ring-blue-500">
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
              <Label htmlFor="date" className="text-right font-medium">
                Date
              </Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className={cn(
                      "col-span-3 justify-start text-left font-normal",
                      !createForm.date && "text-muted-foreground"
                    )}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {createForm.date ? format(createForm.date, "PPP") : <span>Pick a date</span>}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                  <Calendar
                    mode="single"
                    selected={createForm.date || undefined}
                    onSelect={(date: Date | undefined) => setCreateForm({ ...createForm, date: date ?? null })}
                    disabled={(date) => date < new Date(new Date().setHours(0, 0, 0, 0))}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="time" className="text-right font-medium">
                Time
              </Label>
              <Select
                value={createForm.time}
                onValueChange={(value) => setCreateForm({ ...createForm, time: value })}
              >
                <SelectTrigger className="col-span-3 border-gray-300 focus:ring-blue-500">
                  <SelectValue placeholder="Select a time" />
                </SelectTrigger>
                <SelectContent>
                  {generateTimeOptions().map((time) => (
                    <SelectItem key={time} value={time}>
                      {time}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="status" className="text-right font-medium">
                Status
              </Label>
              <Select
                value={createForm.status}
                onValueChange={(value) =>
                  setCreateForm({ ...createForm, status: value as ReservationStatus })
                }
              >
                <SelectTrigger className="col-span-3 border-gray-300 focus:ring-blue-500">
                  <SelectValue placeholder="Select status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={ReservationStatus.PENDING}>Pending</SelectItem>
                  <SelectItem value={ReservationStatus.CONFIRMED}>Confirmed</SelectItem>
                  <SelectItem value={ReservationStatus.CANCELLED}>Cancelled</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsCreateDialogOpen(false)}
              className="border-gray-300 text-gray-700 hover:bg-gray-100"
            >
              Cancel
            </Button>
            <Button
              onClick={handleCreate}
              disabled={isCreating}
              className="bg-blue-600 hover:bg-blue-700 text-white"
            >
              {isCreating ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Create
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog for updating status */}
      <Dialog open={isUpdateDialogOpen} onOpenChange={setIsUpdateDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
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
              <SelectTrigger className="border-gray-300 focus:ring-blue-500">
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
            <Button
              variant="outline"
              onClick={() => setIsUpdateDialogOpen(false)}
              className="border-gray-300 text-gray-700 hover:bg-gray-100"
            >
              Cancel
            </Button>
            <Button
              onClick={handleUpdateStatus}
              disabled={isUpdating || !newStatus}
              className="bg-blue-600 hover:bg-blue-700 text-white"
            >
              {isUpdating ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Save
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog for deleting reservation */}
      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Delete Reservation</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete reservation #{selectedReservation?.reservationId}?
              This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsDeleteDialogOpen(false)}
              className="border-gray-300 text-gray-700 hover:bg-gray-100"
            >
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={handleDelete}
              disabled={isDeleting}
              className="bg-red-600 hover:bg-red-700"
            >
              {isDeleting ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog for viewing order details */}
      <Dialog open={isOrderDialogOpen} onOpenChange={setIsOrderDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Order Details</DialogTitle>
            <DialogDescription>
              Details for order #{selectedReservation?.orderId}
            </DialogDescription>
          </DialogHeader>
          {selectedReservation?.orderId && (
            <div className="py-4 space-y-2">
              <p>
                <strong>Order ID:</strong> {selectedReservation.orderId}
              </p>
              <p>
                <strong>Total Amount:</strong> ${selectedReservation.totalAmount?.toFixed(2) || "0.00"}
              </p>
              <p>
                <strong>Order Status:</strong> {selectedReservation.orderStatus || "N/A"}
              </p>
            </div>
          )}
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsOrderDialogOpen(false)}
              className="border-gray-300 text-gray-700 hover:bg-gray-100"
            >
              Close
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ReservationManagementPage;