/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from 'react';
import { useGetAvailableTablesQuery, useCreateReservationMutation } from '@/services/reservationServices';
import { useGetAllMenuItemsQuery } from '@/services/menuItemServices';
import { useCreateOrderMutation } from '@/services/orderServices';
import { CafeTable } from '@/interfaces/cafetable';
import { MenuItem } from '@/interfaces/menuItem';
import { OrderRequest } from '@/interfaces/order';
import { ReservationStatus } from '@/enums/reservationStatus';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Loader2, Trash2 } from 'lucide-react';
import { toast } from '@/hooks/use-toast';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { format } from 'date-fns';
import { useSelector } from 'react-redux';
import { RootState } from '@/stores';

interface OrderItemRequest {
  itemId: number;
  quantity: number;
}

const ReservationPage: React.FC = () => {
  const user = useSelector((state: RootState) => state.auth.user);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const tableId = parseInt(searchParams.get('tableId') || '0');
  const { data: tablesData, isLoading: isTablesLoading } = useGetAvailableTablesQuery();
  const { data: menuItemsData, isLoading: isMenuItemsLoading } = useGetAllMenuItemsQuery();
  const [createReservation, { isLoading: isCreatingReservation }] = useCreateReservationMutation();
  const [createOrder, { isLoading: isCreatingOrder }] = useCreateOrderMutation();
  const [reservation, setReservation] = useState({
    userId: user?.id ?? 0,
    tableId: tableId || 0,
    reservationTime: format(new Date(), "yyyy-MM-dd'T'HH:mm"),
    status: ReservationStatus.PENDING,
  });
  const [orderItems, setOrderItems] = useState<OrderItemRequest[]>([]);

  const addItem = (itemId: number) => {
    const existingItem = orderItems.find(item => item.itemId === itemId);
    if (existingItem) {
      setOrderItems(orderItems.map(item =>
        item.itemId === itemId ? { ...item, quantity: item.quantity + 1 } : item
      ));
    } else {
      setOrderItems([...orderItems, { itemId, quantity: 1 }]);
    }
  };

  const removeItem = (itemId: number) => {
    setOrderItems(orderItems.filter(item => item.itemId !== itemId));
  };

  const updateQuantity = (itemId: number, quantity: number) => {
    if (quantity < 1) {
      removeItem(itemId);
    } else {
      setOrderItems(orderItems.map(item =>
        item.itemId === itemId ? { ...item, quantity } : item
      ));
    }
  };

  const totalAmount = orderItems.reduce((total, item) => {
    const menuItem = menuItemsData?.data.find(m => m.itemId === item.itemId);
    return total + (menuItem?.price || 0) * item.quantity;
  }, 0);

  const handleSubmit = async () => {
    if (reservation.tableId <= 0) {
      toast({
        title: "Error",
        description: "Please select a table",
        variant: "destructive",
      });
      return;
    }
    if (!reservation.reservationTime) {
      toast({
        title: "Error",
        description: "Please select a reservation time",
        variant: "destructive",
      });
      return;
    }

    try {
      // Tạo Reservation
      const reservationResponse = await createReservation(reservation).unwrap();
      const reservationId = reservationResponse.data.reservationId;
      toast({
        title: "Success",
        description: `Reservation #${reservationId} created successfully`,
      });

      // Chuyển đến PaymentPage nếu có món, hoặc ConfirmationPage nếu không
      if (orderItems.length > 0) {
        const order: OrderRequest = {
          userId: user?.id || 0,
          tableId: reservation.tableId,
          reservationId,
          orderStatus: 'PENDING',
          orderItems,
        };
        const orderResponse = await createOrder(order).unwrap();
        const orderId = orderResponse.data.orderId;
        toast({
          title: "Success",
          description: `Order #${orderId} created successfully`,
        });

        navigate(`/profile`);
      } else {
        navigate(`/`);
      }
    } catch (err: any) {
      const errorMessage = err.message || err.data?.message || "Failed to create reservation or order";
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
    }
  };

  if (isTablesLoading || isMenuItemsLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  const tables = tablesData?.data || [];
  const menuItems = menuItemsData?.data || [];

  return (
    <div className="container mx-auto mt-6 p-6 max-w-2xl">
      <h1 className="text-2xl font-bold mb-6">Reserve a Table</h1>
      <div className="grid gap-6 md:grid-cols-2">
        {/* Reservation Form */}
        <Card>
          <CardHeader>
            <CardTitle>Reservation Details</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div>
              <label className="text-sm font-medium">Table</label>
              <Select
                value={reservation.tableId.toString()}
                onValueChange={(value) => setReservation({ ...reservation, tableId: parseInt(value) })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select a table" />
                </SelectTrigger>
                <SelectContent>
                  {tables.map((table: CafeTable) => (
                    <SelectItem key={table.tableId} value={table.tableId.toString()}>
                      Table {table.tableNumber} (Capacity: {table.capacity})
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div>
              <label className="text-sm font-medium">Reservation Time</label>
              <Input
                type="datetime-local"
                value={reservation.reservationTime}
                onChange={(e) => setReservation({ ...reservation, reservationTime: e.target.value })}
              />
            </div>
          </CardContent>
        </Card>

        {/* Order Form */}
        <Card className='w-[400px]'>
          <CardHeader>
            <CardTitle>Add Items (Optional)</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4">
            <Select
              onValueChange={(value) => addItem(parseInt(value))}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select a menu item" />
              </SelectTrigger>
              <SelectContent>
                {menuItems.map((item: MenuItem) => (
                  <SelectItem key={item.itemId} value={item.itemId.toString()}>
                    {item.itemName} (${item.price.toFixed(2)})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <div className="mt-4 space-y-4">
              {orderItems.map((item) => {
                const menuItem = menuItems.find(m => m.itemId === item.itemId);
                return (
                  <div key={item.itemId} className="flex items-center gap-4 border-b pb-2">
                    {menuItem?.imageUrl && (
                      <img
                        src={menuItem.imageUrl}
                        alt={menuItem.itemName}
                        className="w-16 h-16 object-cover rounded-md"
                      />
                    )}
                    <div className="flex-1">
                      <p className="font-medium">{menuItem?.itemName}</p>
                      <p className="text-sm text-gray-600">${menuItem?.price.toFixed(2)} each</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Input
                        type="number"
                        value={item.quantity}
                        onChange={(e) => updateQuantity(item.itemId, parseInt(e.target.value) || 1)}
                        className="w-16"
                        min="1"
                      />
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => removeItem(item.itemId)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                );
              })}
            </div>
            {orderItems.length > 0 && (
              <p className="font-semibold text-lg">Total: ${totalAmount.toFixed(2)}</p>
            )}
          </CardContent>
        </Card>

        {/* Submit Buttons */}
        <div className="flex justify-end gap-2 md:col-span-2">
          <Button
            variant="outline"
            onClick={() => navigate('/')}
          >
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={isCreatingReservation || isCreatingOrder}
          >
            {(isCreatingReservation || isCreatingOrder) && (
              <Loader2 className="h-4 w-4 animate-spin mr-2" />
            )}
            Reserve {orderItems.length > 0 ? 'and Order' : ''}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ReservationPage;