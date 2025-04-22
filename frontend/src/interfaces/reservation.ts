import { CafeTable } from "./cafetable";
import { User } from "./user";

export interface Reservation {
  reservationId: number;
  user: User;
  cafeTable: CafeTable;
  reservationTime: string;
  status: ReservationStatus;
  createdAt: string;
  updatedAt: string;
  order?: Order;
}

export interface Order {
  orderId: number;
  totalAmount: number;
  orderStatus: OrderStatus;
  paymentStatus: PaymentStatus;
}

export enum ReservationStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  CANCELLED = "CANCELLED",
}

export enum OrderStatus {
  PENDING = "PENDING",
  PREPARING = "PREPARING",
  SERVED = "SERVED",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED",
}

export enum PaymentStatus {
  UNPAID = "UNPAID",
  PARTIALLY_PAID = "PARTIALLY_PAID",
  PAID = "PAID",
  CANCELLED = "CANCELLED",
}
