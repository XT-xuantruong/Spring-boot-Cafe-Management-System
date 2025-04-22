export interface CafeTable {
  tableId: number;
  tableNumber: string;
  capacity: number; // Sức chứa
  status: string; //  AVAILABLE, OCCUPIED, RESERVED
  createdAt: string;
  updatedAt: string;
}

export interface CafeTableRequest {
  tableNumber: string;
  capacity: number;
  status?: string;
}
