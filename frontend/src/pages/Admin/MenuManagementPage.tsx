import React, { useState } from 'react';
import { 
  useGetAllMenuItemsQuery, 
  useCreateMenuItemMutation, 
  useUpdateMenuItemMutation, 
  useDeleteMenuItemMutation 
} from '@/services/menuItemServices';
import { MenuItem, MenuItemRequest } from '@/interfaces/menuItem';
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
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { toast } from '@/hooks/use-toast';
import { Pencil, Trash2, Plus } from 'lucide-react';

const MenuManagementPage = () => {
  const { data: menuItems, isLoading, error } = useGetAllMenuItemsQuery();
  const [createMenuItem] = useCreateMenuItemMutation();
  const [updateMenuItem] = useUpdateMenuItemMutation();
  const [deleteMenuItem] = useDeleteMenuItemMutation();

  const [open, setOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [currentMenuItem, setCurrentMenuItem] = useState<MenuItem | null>(null);
  const [formData, setFormData] = useState<MenuItemRequest>({
    itemName: '',
    price: 0,
    description: '',
  });
  const [imageFile, setImageFile] = useState<File | null>(null);

  const handleOpenDialog = (menuItem?: MenuItem) => {
    if (menuItem) {
      setIsEdit(true);
      setCurrentMenuItem(menuItem);
      setFormData({
        itemName: menuItem.itemName,
        price: menuItem.price,
        description: menuItem.description,
      });
      setImageFile(null); // Reset image file for edit
    } else {
      setIsEdit(false);
      setFormData({ itemName: '', price: 0, description: '' });
      setImageFile(null);
    }
    setOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (isEdit && currentMenuItem) {
        await updateMenuItem({ 
          id: currentMenuItem.itemId.toString(), 
          menuItem: formData,
          image: imageFile || undefined,
        }).unwrap();
        toast({
          title: "Success",
          description: "Menu item updated successfully",
        });
      } else {
        await createMenuItem({ 
          menuItem: formData,
          image: imageFile || undefined,
        }).unwrap();
        toast({
          title: "Success",
          description: "Menu item created successfully",
        });
      }
      setOpen(false);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to save menu item",
        variant: "destructive",
      });
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm("Are you sure you want to delete this menu item?")) {
      try {
        await deleteMenuItem(id.toString()).unwrap();
        toast({
          title: "Success",
          description: "Menu item deleted successfully",
        });
      } catch (error) {
        toast({
          title: "Error",
          description: "Failed to delete menu item",
          variant: "destructive",
        });
      }
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setImageFile(e.target.files[0]);
    }
  };

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error loading menu items</div>;

  return (
    <div className="w-full">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Menu Management</h1>
        <Button 
          className="bg-black text-white hover:bg-gray-800"
          onClick={() => handleOpenDialog()}
        >
          <Plus className="mr-2 h-4 w-4" /> Add Menu Item
        </Button>
      </div>

      <div className="w-full overflow-x-auto">
        <Table className="w-full">
          <TableHeader>
            <TableRow>
              <TableHead className="font-medium text-gray-500">Name</TableHead>
              <TableHead className="font-medium text-gray-500">Price</TableHead>
              <TableHead className="font-medium text-gray-500">Description</TableHead>
              <TableHead className="font-medium text-gray-500">Image</TableHead>
              <TableHead className="font-medium text-gray-500">Created At</TableHead>
              <TableHead className="font-medium text-gray-500">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {menuItems?.data.map((item) => (
              <TableRow key={item.itemId}>
                <TableCell>{item.itemName}</TableCell>
                <TableCell>{item.price}</TableCell>
                <TableCell>{item.description}</TableCell>
                <TableCell>
                  {item.imageUrl ? (
                    <img src={item.imageUrl} alt={item.itemName} className="w-16 h-16 object-cover" />
                  ) : (
                    "No Image"
                  )}
                </TableCell>
                <TableCell>
                  {new Date(item.createdAt).toLocaleDateString()}
                </TableCell>
                <TableCell>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenDialog(item)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleDelete(item.itemId)}
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
              {isEdit ? "Edit Menu Item" : "Create New Menu Item"}
            </DialogTitle>
            <DialogDescription>
              {isEdit
                ? "Update the menu item details below."
                : "Enter the details for the new menu item."}
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
                  value={formData.itemName}
                  onChange={(e) =>
                    setFormData({ ...formData, itemName: e.target.value })
                  }
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="price" className="text-right">
                  Price
                </Label>
                <Input
                  id="price"
                  type="number"
                  value={formData.price}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      price: parseFloat(e.target.value),
                    })
                  }
                  className="col-span-3"
                  required
                  min="0"
                  step="0.01"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="description" className="text-right">
                  Description
                </Label>
                <Input
                  id="description"
                  value={formData.description}
                  onChange={(e) =>
                    setFormData({ ...formData, description: e.target.value })
                  }
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="image" className="text-right">
                  Image
                </Label>
                <div className="col-span-3 flex flex-col gap-2">
                  {isEdit && currentMenuItem?.imageUrl && !imageFile && (
                    <img
                      src={currentMenuItem.imageUrl}
                      alt="Current"
                      className="w-24 h-24 object-cover"
                    />
                  )}
                  <Input
                    id="image"
                    type="file"
                    accept="image/*"
                    onChange={handleImageChange}
                    className="col-span-3"
                  />
                </div>
              </div>
            </div>
            <DialogFooter>
              <Button type="submit">
                {isEdit ? "Update Menu Item" : "Create Menu Item"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default MenuManagementPage;