/* eslint-disable @typescript-eslint/no-explicit-any */
import { baseRestApi } from "./baseRestApi";
import { Reservation } from "@/interfaces/reservation";
import { ApiResponse } from "@/interfaces/apiResponse";
import { ReservationStatus } from "@/interfaces/reservation";
import { CafeTable } from "@/interfaces/cafetable";

const entity = "reservations";

export const reservationServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    getAllReservations: builder.query<
      { data: Reservation[]; message: string },
      void
    >({
      query: () => ({
        url: `${entity}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<Reservation[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["Reservations"],
    }),

    getReservationById: builder.query<
      { data: Reservation; message: string },
      string
    >({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<Reservation>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: (result, error, id) => [{ type: "Reservations", id }],
    }),

    getReservationsByUser: builder.query<
      { data: Reservation[]; message: string },
      number
    >({
      query: (userId) => ({
        url: `${entity}/customer`,
        method: "GET",
        params: { userId },
      }),
      transformResponse: (response: ApiResponse<Reservation[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["Reservations"],
    }),

    getAvailableTables: builder.query<
      { data: CafeTable[]; message: string },
      void
    >({
      query: () => ({
        url: `tables`,
        method: "GET",
        params: { status: "AVAILABLE" },
      }),
      transformResponse: (response: ApiResponse<CafeTable[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["CafeTables"],
    }),

    createReservation: builder.mutation<
      { data: Reservation; message: string },
      { tableId: number; reservationTime: string }
    >({
      query: (body) => ({
        url: `${entity}`,
        method: "POST",
        body,
      }),
      transformResponse: (response: ApiResponse<Reservation>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["Reservations"],
    }),

    updateReservationStatus: builder.mutation<
      { data: Reservation; message: string },
      { id: string; status: ReservationStatus }
    >({
      query: ({ id, status }) => ({
        url: `${entity}/${id}`,
        method: "PUT",
        body: { status },
      }),
      transformResponse: (response: ApiResponse<Reservation>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: (result, error, { id }) => [
        "Reservations",
        { type: "Reservations", id },
      ],
    }),

    deleteReservation: builder.mutation<{ message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "DELETE",
      }),
      transformResponse: (response: ApiResponse<null>) => ({
        message: response.message,
      }),
      invalidatesTags: ["Reservations"],
    }),
  }),
});

export const {
  useGetAllReservationsQuery,
  useGetReservationByIdQuery,
  useGetReservationsByUserQuery,
  useGetAvailableTablesQuery,
  useCreateReservationMutation,
  useUpdateReservationStatusMutation,
  useDeleteReservationMutation,
} = reservationServices;
