/* eslint-disable @typescript-eslint/no-explicit-any */
import { ApiResponse } from "@/interfaces/apiResponse";
import { baseRestApi } from "./baseRestApi";
import { Payment, PaymentRequest } from "@/interfaces/payment";
import { PaymentStatus } from "@/enums/paymentStatus";

const entity = "payments";

export const paymentServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    getAllPayments: builder.query<ApiResponse<Payment[]>, void>({
      query: () => ({
        url: `${entity}`,
        method: "GET",
      }),
      providesTags: ["Payments"],
    }),
    getPaymentsByOrder: builder.query<ApiResponse<Payment[]>, number>({
      query: (orderId) => ({
        url: `${entity}/order/${orderId}`,
        method: "GET",
      }),
      providesTags: ["Payments"],
    }),
    getPaymentsByUser: builder.query<ApiResponse<Payment[]>, number>({
      query: (userId) => ({
        url: `${entity}/customer`,
        method: "GET",
        params: { userId },
      }),
      providesTags: ["Payments"],
    }),
    processPayment: builder.mutation<ApiResponse<Payment>, PaymentRequest>({
      query: (body) => ({
        url: `${entity}`,
        method: "POST",
        body,
      }),
      transformErrorResponse: (response: ApiResponse<any>) => ({
        status: response.status,
        message: response.data?.message || "Failed to process payment",
      }),
      invalidatesTags: ["Payments", "Orders"],
    }),
    updatePaymentStatus: builder.mutation<
      ApiResponse<Payment>,
      { id: number; paymentStatus: PaymentStatus }
    >({
      query: ({ id, paymentStatus }) => ({
        url: `${entity}/${id}`,
        method: "PUT",
        body: paymentStatus,
      }),
      invalidatesTags: ["Payments", "Orders"],
    }),
    deletePayment: builder.mutation<ApiResponse<void>, number>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Payments", "Orders"],
    }),
  }),
});

export const {
  useGetAllPaymentsQuery,
  useGetPaymentsByOrderQuery,
  useGetPaymentsByUserQuery,
  useProcessPaymentMutation,
  useUpdatePaymentStatusMutation,
  useDeletePaymentMutation,
} = paymentServices;
