import { baseRestApi } from "./baseRestApi";
import { Order, OrderRequest } from "@/interfaces/order";
import { ApiResponse } from "@/interfaces/apiResponse";
import { OrderStatus } from "@/enums/orderStatus";

const entity = "orders";

export const orderServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    getAllOrders: builder.query<{ data: Order[]; message: string }, void>({
      query: () => ({
        url: `${entity}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<Order[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["Orders"],
    }),

    getOrderById: builder.query<{ data: Order; message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<Order>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: (_result, _error, id) => [{ type: "Orders", id }],
    }),

    getOrdersByUser: builder.query<{ data: Order[]; message: string }, number>({
      query: (userId) => ({
        url: `${entity}/user/${userId}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<Order[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["Orders"],
    }),

    createOrder: builder.mutation<
      { data: Order; message: string },
      OrderRequest
    >({
      query: (body) => ({
        url: `${entity}`,
        method: "POST",
        body,
      }),
      transformResponse: (response: ApiResponse<Order>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["Orders", "Reservations"],
    }),

    updateOrderStatus: builder.mutation<
      { data: Order; message: string },
      { id: string; status: OrderStatus }
    >({
      query: ({ id, status }) => ({
        url: `${entity}/${id}/status`,
        method: "PUT",
        body: status,
      }),
      transformResponse: (response: ApiResponse<Order>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        "Orders",
        { type: "Orders", id },
        "Reservations",
      ],
    }),

    cancelOrder: builder.mutation<{ message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "DELETE",
      }),
      transformResponse: (response: ApiResponse<null>) => ({
        message: response.message,
      }),
      invalidatesTags: ["Orders", "Reservations"],
    }),
  }),
});

export const {
  useGetAllOrdersQuery,
  useGetOrderByIdQuery,
  useGetOrdersByUserQuery,
  useCreateOrderMutation,
  useUpdateOrderStatusMutation,
  useCancelOrderMutation,
} = orderServices;
