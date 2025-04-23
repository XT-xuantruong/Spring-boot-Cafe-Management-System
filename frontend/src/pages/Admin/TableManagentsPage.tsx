/* eslint-disable @typescript-eslint/no-unused-vars */
import React, { useState } from 'react';
import { 
  useGetAllTablesQuery, 
  useCreateTableMutation, 
  useUpdateTableMutation, 
  useDeleteTableMutation 
} from '@/services/cafeTableServices';
import { CafeTable, CafeTableRequest } from '@/interfaces/cafetable';
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
import { User } from '@/interfaces/user';
import { RootState } from '@/stores';
import { useSelector } from 'react-redux';
import { TableStatus } from '@/enums/tableStatus';

const TableManagementPage = () => {
  const { data: tables, isLoading, error } = useGetAllTablesQuery();
  const user = useSelector((state: RootState) => state.auth.user as User | null);
  const [createTable] = useCreateTableMutation();
  const [updateTable] = useUpdateTableMutation();
  const [deleteTable] = useDeleteTableMutation();

  const [open, setOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [currentTable, setCurrentTable] = useState<CafeTable | null>(null);
  const [formData, setFormData] = useState<CafeTableRequest>({
    tableNumber: '',
    capacity: 0,
    status: TableStatus.AVAILABLE,
  });
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [tableToDelete, setTableToDelete] = useState<number | null>(null);

  const handleOpenDialog = (table?: CafeTable) => {
    if (table) {
      setIsEdit(true);
      setCurrentTable(table);
      setFormData({
        tableNumber: table.tableNumber,
        capacity: table.capacity,
        status: table.status,
      });
    } else {
      setIsEdit(false);
      setFormData({ tableNumber: '', capacity: 0, status: TableStatus.AVAILABLE });
    }
    setOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (isEdit && currentTable) {
        await updateTable({ 
          id: currentTable.tableId.toString(), 
          data: formData 
        }).unwrap();
        toast({
          title: "Success",
          description: "Table updated successfully",
        });
      } else {
        await createTable(formData).unwrap();
        toast({
          title: "Success",
          description: "Table created successfully",
        });
      }
      setOpen(false);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to save table",
        variant: "destructive",
      });
    }
  };

  const handleOpenDeleteDialog = (id: number) => {
    setTableToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDelete = async () => {
    if (tableToDelete) {
      try {
        await deleteTable(tableToDelete.toString()).unwrap();
        toast({
          title: "Success",
          description: "Table deleted successfully",
        });
      } catch (error) {
        toast({
          title: "Error",
          description: "Failed to delete table",
          variant: "destructive",
        });
      } finally {
        setDeleteDialogOpen(false);
        setTableToDelete(null);
      }
    }
  };

  if (isLoading) return <div className="flex justify-center items-center h-96">Loading...</div>;
  if (error) return <div className="text-center text-red-500 p-6">Error loading tables</div>;

  return (
    <div className="p-6 bg-white rounded-lg shadow max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold ">Table Management</h1>
        <Button 
          disabled={user?.role==="STAFF"}
          className=" text-white"
          onClick={() => handleOpenDialog()}
        >
          <Plus className="mr-2 h-4 w-4" /> Add Table
        </Button>
      </div>

      <div className="w-full overflow-x-auto">
        <Table className="w-full text-center">
          <TableHeader>
            <TableRow>
              <TableHead className="font-medium text-center text-gray-500">Table Number</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Capacity</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Status</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Created At</TableHead>
              <TableHead className="font-medium text-center text-gray-500">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {tables?.data.map((table) => (
              <TableRow key={table.tableId}>
                <TableCell>{table.tableNumber}</TableCell>
                <TableCell>{table.capacity}</TableCell>
                <TableCell>
                  <Badge
                    variant="outline"
                    className={
                      table.status === 'AVAILABLE'
                        ? 'bg-green-100 text-green-800'
                        : table.status === 'OCCUPIED'
                        ? 'bg-red-100 text-red-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }
                  >
                    {table.status}
                  </Badge>
                </TableCell>
                <TableCell>
                  {new Date(table.createdAt).toLocaleDateString()}
                </TableCell>
                <TableCell>
                  <div className="flex gap-2 justify-center">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenDialog(table)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      disabled={user?.role==="STAFF"}
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenDeleteDialog(table.tableId)}
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
              {isEdit ? "Edit Table" : "Create New Table"}
            </DialogTitle>
            <DialogDescription>
              {isEdit
                ? "Update the table details below."
                : "Enter the details for the new table."}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="tableNumber" className="text-right">
                  Table Number
                </Label>
                <Input
                  id="tableNumber"
                  value={formData.tableNumber}
                  onChange={(e) =>
                    setFormData({ ...formData, tableNumber: e.target.value })
                  }
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="capacity" className="text-right">
                  Capacity
                </Label>
                <Input
                  id="capacity"
                  type="number"
                  value={formData.capacity}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      capacity: parseInt(e.target.value) || 0,
                    })
                  }
                  className="col-span-3"
                  required
                  min="1"
                />
              </div>
              {
                isEdit && 
                  <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="status" className="text-right">
                    Status
                  </Label>
                  <Select
                    value={formData.status}
                    onValueChange={(value) =>
                      setFormData({ ...formData, status: value })
                    }
                  >
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Select status" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value={TableStatus.AVAILABLE}>Available</SelectItem>
                      <SelectItem value={TableStatus.OCCUPIED}>Occupied</SelectItem>
                      <SelectItem value={TableStatus.RESERVED}>Reserved</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              }
            </div>
            <DialogFooter>
              <Button type="submit" className="bg-[#1E3A8A] text-white hover:bg-[#1E3A8A]/90">
                {isEdit ? "Update Table" : "Create Table"}
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
              Are you sure you want to delete this table? This action cannot be undone.
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

export default TableManagementPage;