import { OrderStatus } from "@/enums/orderStatus";
import { ReservationStatus } from "@/enums/reservationStatus";

export interface Reservation {
  reservationId: number;
  userId: number;
  userName: string;
  tableId: number;
  tableNumber: string;
  reservationTime: string;
  status: ReservationStatus;
  orderId?: number;
  totalAmount?: number;
  orderStatus?: OrderStatus;
  createdAt: string;
  updatedAt: string;
}
