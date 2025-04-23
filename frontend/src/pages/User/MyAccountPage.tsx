/* eslint-disable @typescript-eslint/no-unused-vars */
import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
  useGetReservationsByUserQuery,
} from '@/services/reservationServices'; // Assume updateProfile is exported here
import { RootState } from '@/stores';
import { setUser } from '@/stores/authSlice';
import { User, UserUpdate } from '@/interfaces/user';
import { Reservation } from '@/interfaces/reservation';
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { useUpdateProfileMutation } from '@/services/UserSerivces';

const MyAccountPage: React.FC = () => {
  const dispatch = useDispatch();
  const user = useSelector((state: RootState) => state.auth.user);
  const userId = user?.id;

  // State for profile form
  const [formData, setFormData] = useState<UserUpdate>({
    name: user?.name || '',
    phone: user?.phone || '',
    address: user?.address || '',
  });

  // State for file upload
  const [avatarFile, setAvatarFile] = useState<File | null>(null);

  // RTK Query mutation hook
  const [updateProfile, { isLoading: isUpdating }] = useUpdateProfileMutation();

  // Fetch reservations
  const { data: reservationsData, isLoading: reservationsLoading } =
    useGetReservationsByUserQuery(userId!, { skip: !userId });

  // Handle text input changes
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Handle file input change
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setAvatarFile(file);
      // Optionally preview the image
      setFormData((prev) => ({ ...prev, avatarUrl: URL.createObjectURL(file) }));
    }
  };

  // Handle profile form submission
  const handleProfileSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const data: UserUpdate = {
        name: formData.name,
        phone: formData.phone || null,
        address: formData.address || null,
      };

      // Use the updateProfile mutation
      const response = await updateProfile({ data, file: avatarFile || undefined }).unwrap();
      dispatch(setUser({ user: response.data }));
      alert('Profile updated successfully!');
    } catch (error) {
      console.error('Profile update failed:', error);
      alert('Failed to update profile.');
    }
  };

  return (
    <div className="container mx-auto p-6 mt-6">
      <h1 className="text-3xl font-bold mb-6">My Account</h1>

      <Tabs defaultValue="profile" className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="profile">Profile</TabsTrigger>
          <TabsTrigger value="reservations">Reservations & Orders</TabsTrigger>
        </TabsList>

        {/* Profile Tab */}
        <TabsContent value="profile">
          <Card>
            <CardHeader>
              <CardTitle>Edit Profile</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleProfileSubmit} className="space-y-4">
                <div className="flex items-center space-x-4 mb-4">
                  <Avatar className="h-16 w-16">
                    <AvatarImage src={formData.avatarUrl || user?.avatarUrl || ''} />
                    <AvatarFallback>{user?.name.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div>
                    <Label htmlFor="avatar">Upload Avatar</Label>
                    <Input
                      id="avatar"
                      type="file"
                      accept="image/*"
                      onChange={handleFileChange}
                    />
                  </div>
                </div>
                <div>
                  <Label htmlFor="name">Name</Label>
                  <Input
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div>
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    name="email"
                    type="email"
                    value={user?.email}
                    disabled
                  />
                </div>
                <div>
                  <Label htmlFor="phone">Phone</Label>
                  <Input
                    id="phone"
                    name="phone"
                    value={formData.phone || ''}
                    onChange={handleInputChange}
                  />
                </div>
                <div>
                  <Label htmlFor="address">Address</Label>
                  <Input
                    id="address"
                    name="address"
                    value={formData.address || ''}
                    onChange={handleInputChange}
                  />
                </div>
                <Button type="submit" disabled={isUpdating}>
                  {isUpdating ? 'Saving...' : 'Save Changes'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Reservations & Orders Tab */}
        <TabsContent value="reservations">
          <div className="space-y-8">
            {/* Reservations Section */}
            <Card>
              <CardHeader>
                <CardTitle>My Reservations</CardTitle>
              </CardHeader>
              <CardContent>
                {reservationsLoading ? (
                  <p>Loading reservations...</p>
                ) : reservationsData?.data.length ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Table</TableHead>
                        <TableHead>Time</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Order Amount</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {reservationsData.data.map((reservation: Reservation) => (
                        <TableRow key={reservation.reservationId}>
                          <TableCell>{reservation.tableNumber}</TableCell>
                          <TableCell>
                            {new Date(reservation.reservationTime).toLocaleString()}
                          </TableCell>
                          <TableCell>{reservation.status}</TableCell>
                          <TableCell>
                            {reservation.totalAmount
                              ? `$${reservation.totalAmount.toFixed(2)}`
                              : 'N/A'}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <p>No reservations found.</p>
                )}
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default MyAccountPage;