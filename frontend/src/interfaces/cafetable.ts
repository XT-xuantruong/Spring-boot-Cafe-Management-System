import { TableStatus } from "@/enums/tableStatus";

export interface CafeTable {
  tableId: number;
  tableNumber: string;
  capacity: number; // Sức chứa
  status: TableStatus; //  AVAILABLE, OCCUPIED, RESERVED
  createdAt: string;
  updatedAt: string;
}

export interface CafeTableRequest {
  tableNumber: string;
  capacity: number;
  status?: TableStatus;
}
