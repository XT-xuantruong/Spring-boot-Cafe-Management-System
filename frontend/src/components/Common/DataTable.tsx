import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";

interface Column<T> {
  header: string;
  accessor: keyof T | ((item: T) => string | number);
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  onEdit?: (item: T) => void;
  onDelete?: (item: T) => void;
}

export const DataTable = <T,>({ columns, data, onEdit, onDelete }: DataTableProps<T>) => {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          {columns.map((column) => (
            <TableHead key={column.header}>{column.header}</TableHead>
          ))}
          {(onEdit || onDelete) && <TableHead>Actions</TableHead>}
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.map((item, index) => (
          <TableRow key={index}>
            {columns.map((column) => (
              <TableCell key={column.header}>
                {typeof column.accessor === "function"
                  ? String(column.accessor(item))
                  : String(item[column.accessor])}
              </TableCell>
            ))}
            {(onEdit || onDelete) && (
              <TableCell>
                {onEdit && <Button onClick={() => onEdit(item)} className="mr-2">Edit</Button>}
                {onDelete && <Button variant="destructive" onClick={() => onDelete(item)}>Delete</Button>}
              </TableCell>
            )}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};