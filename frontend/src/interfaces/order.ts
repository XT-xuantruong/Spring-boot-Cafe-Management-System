import { OrderStatus } from "@/enums/orderStatus";
import { PaymentMethod } from "@/enums/paymentMethod";
import { PaymentStatus } from "@/enums/paymentStatus";

export interface OrderItem {
  orderItemId: number;
  itemId: number;
  itemName: string;
  imageUrl?: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Payment {
  paymentId: number;
  orderId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  paymentTime?: string;
  transactionId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Order {
  orderId: number;
  userId: number;
  userName: string;
  tableId: number;
  tableNumber: string;
  reservationId?: number;
  orderStatus: OrderStatus;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItem[];
  payment?: Payment;
}

export interface OrderRequest {
  userId: number;
  tableId: number;
  reservationId?: number;
  orderStatus: string;
  orderItems: { itemId: number; quantity: number }[];
}
