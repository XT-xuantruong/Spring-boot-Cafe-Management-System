import { PaymentMethod } from "@/enums/paymentMethod";
import { PaymentStatus } from "@/enums/paymentStatus";

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

export interface PaymentRequest {
  orderId: number;
  amount: number;
  paymentMethod: string;
  transactionId?: string;
}
