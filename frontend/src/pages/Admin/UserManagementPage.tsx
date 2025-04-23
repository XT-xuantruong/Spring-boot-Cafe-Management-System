/* eslint-disable @typescript-eslint/no-unused-vars */
import React, { useState } from 'react';
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { toast } from '@/hooks/use-toast';
import { Pencil, Trash2, Plus } from 'lucide-react';
import { useCreateUserMutation, useDeleteUserMutation, useGetAllUsersQuery, useUpdateUserMutation } from '@/services/UserSerivces';
import { User, UserRequest, UserUpdate } from '@/interfaces/user';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { format } from 'date-fns';

const UserManagementPage = () => {
  const { data: users, isLoading, error } = useGetAllUsersQuery();
  const [createUser] = useCreateUserMutation();
  const [updateUser] = useUpdateUserMutation();
  const [deleteUser] = useDeleteUserMutation();

  const [open, setOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [formData, setFormData] = useState<UserRequest>({
    name: "",
    email: "",
    password: "",
    phone: null,
    address: null,
    role: "STAFF",
  });
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<number | null>(null);

  const handleOpenDialog = (user?: User) => {
    if (user) {
      setIsEdit(true);
      setCurrentUser(user);
      setFormData({
        name: user.name,
        email: user.email,
        password: "",
        phone: user.phone,
        address: user.address,
        role: user.role,
      });
    } else {
      setIsEdit(false);
      setCurrentUser(null);
      setFormData({
        name: "",
        email: "",
        password: "",
        phone: null,
        address: null,
        role: "STAFF",
      });
    }
    setOpen(true);
  };

  const validateForm = () => {
    if (!formData.name) {
      toast({
        title: "Error",
        description: "Name is required",
        variant: "destructive",
      });
      return false;
    }
    if (!formData.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      toast({
        title: "Error",
        description: "Valid email is required",
        variant: "destructive",
      });
      return false;
    }
    if (!isEdit && !formData.password) {
      toast({
        title: "Error",
        description: "Password is required for new user",
        variant: "destructive",
      });
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      if (isEdit && currentUser) {
        const updateData: UserUpdate = {
          name: formData.name,
          phone: formData.phone,
          address: formData.address,
          role: formData.role,
          password: formData.password || undefined,
        };
        await updateUser({ id: currentUser.id.toString(), data: updateData }).unwrap();
        toast({
          title: "Success",
          description: "User updated successfully",
        });
      } else {
        await createUser(formData).unwrap();
        toast({
          title: "Success",
          description: "User created successfully",
        });
      }
      setOpen(false);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to save user",
        variant: "destructive",
      });
    }
  };

  const handleOpenDeleteDialog = (id: number) => {
    setUserToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDelete = async () => {
    if (userToDelete) {
      try {
        await deleteUser(userToDelete.toString()).unwrap();
        toast({
          title: "Success",
          description: "User deleted successfully",
        });
      } catch (error) {
        toast({
          title: "Error",
          description: "Failed to delete user",
          variant: "destructive",
        });
      } finally {
        setDeleteDialogOpen(false);
        setUserToDelete(null);
      }
    }
  };

  if (isLoading) return <div className="flex justify-center items-center h-96">Loading...</div>;
  if (error) return <div className="text-center text-red-500 p-6">Error loading users</div>;

  return (
    <div className="p-6 bg-white rounded-lg shadow max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">User Management</h1>
        <Button
          className="text-white"
          onClick={() => handleOpenDialog()}
        >
          <Plus className="mr-2 h-4 w-4" /> Add User
        </Button>
      </div>

      <div className="w-full overflow-x-auto">
        <Table className="w-full text-center">
          <TableHeader>
            <TableRow>
              <TableHead className="font-medium text-center text-gray-500">Name</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Avatar</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Email</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Phone</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Role</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Created At</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {users?.data.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.name}</TableCell>
                <TableCell>
                  <Avatar className="h-10 w-10 border-2 border-[#60A5FA] shadow-[0_0_8px_#60A5FA] hover:scale-110 transition-transform">
                    <AvatarImage src={user.avatarUrl ?? undefined} alt={user.name} />
                    <AvatarFallback className="bg-[#60A5FA] text-white">
                      {user.name || 'U'}
                    </AvatarFallback>
                  </Avatar>
                </TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>{user.phone}</TableCell>
                <TableCell>
                  <Badge
                    variant="outline"
                    className={
                      user.role === 'ADMIN'
                        ? 'bg-blue-100 text-blue-800'
                        : user.role === 'STAFF'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }
                  >
                    {user.role}
                  </Badge>
                </TableCell>
                <TableCell>
                  {format(new Date(user.createdAt), 'dd/MM/yyyy HH:mm')}
                </TableCell>
                <TableCell>
                  <div className="flex gap-2 justify-center">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenDialog(user)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenDeleteDialog(user.id)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {isEdit ? "Edit User" : "Create New User"}
            </DialogTitle>
            <DialogDescription>
              {isEdit
                ? "Update the user details below."
                : "Enter the details for the new user."}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="name" className="text-right">
                  Name
                </Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="email" className="text-right">
                  Email
                </Label>
                <Input
                  id="email"
                  type="email"
                  value={formData.email}
                  onChange={(e) =>
                    setFormData({ ...formData, email: e.target.value })
                  }
                  className="col-span-3"
                  required
                  disabled={isEdit}
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="password" className="text-right">
                  Password
                </Label>
                <Input
                  id="password"
                  type="password"
                  value={formData.password}
                  onChange={(e) =>
                    setFormData({ ...formData, password: e.target.value })
                  }
                  className="col-span-3"
                  required={!isEdit}
                  placeholder={isEdit ? "Leave blank to keep current password" : ""}
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="phone" className="text-right">
                  Phone
                </Label>
                <Input
                  id="phone"
                  value={formData.phone ?? ""}
                  onChange={(e) =>
                    setFormData({ ...formData, phone: e.target.value || null })
                  }
                  className="col-span-3"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="address" className="text-right">
                  Address
                </Label>
                <Input
                  id="address"
                  value={formData.address ?? ""}
                  onChange={(e) =>
                    setFormData({ ...formData, address: e.target.value || null })
                  }
                  className="col-span-3"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="role" className="text-right">
                  Role
                </Label>
                <Select
                  value={formData.role ?? undefined}
                  onValueChange={(value) =>
                    setFormData({ ...formData, role: value })
                  }
                >
                  <SelectTrigger className="col-span-3">
                    <SelectValue placeholder="Select role" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ADMIN">Admin</SelectItem>
                    <SelectItem value="STAFF">Staff</SelectItem>
                    <SelectItem value="CUSTOMER">Customer</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <DialogFooter>
              <Button type="submit" className="bg-[#1E3A8A] text-white hover:bg-[#1E3A8A]/90">
                {isEdit ? "Update User" : "Create User"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm Delete</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this user? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setDeleteDialogOpen(false)}
            >
              Cancel
            </Button>
            <Button
              className="bg-red-600 text-white hover:bg-red-700"
              onClick={handleDelete}
            >
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default UserManagementPage;