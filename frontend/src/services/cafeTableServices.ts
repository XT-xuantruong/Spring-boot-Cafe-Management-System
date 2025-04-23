import { baseRestApi } from "./baseRestApi";
import { CafeTable, CafeTableRequest } from "@/interfaces/cafetable";
import { ApiResponse } from "@/interfaces/apiResponse";

const entity = "tables";

export const cafeTableServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    // Lấy danh sách tất cả bàn
    getAllTables: builder.query<{ data: CafeTable[]; message: string }, void>({
      query: () => ({
        url: `${entity}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<CafeTable[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["CafeTables"], // Để invalidate cache khi cần
    }),

    // Lấy bàn theo ID
    getTableById: builder.query<{ data: CafeTable; message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<CafeTable>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: (_result, _error, id) => [{ type: "CafeTables", id }], // Cache theo ID
    }),

    // Tạo bàn mới
    createTable: builder.mutation<
      { data: CafeTable; message: string },
      CafeTableRequest
    >({
      query: (data) => ({
        url: `${entity}`,
        method: "POST",
        body: data,
      }),
      transformResponse: (response: ApiResponse<CafeTable>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["CafeTables"], // Làm mới danh sách bàn sau khi tạo
    }),

    // Cập nhật thông tin bàn
    updateTable: builder.mutation<
      { data: CafeTable; message: string },
      { id: string; data: CafeTableRequest }
    >({
      query: ({ id, data }) => ({
        url: `${entity}/${id}`,
        method: "PUT",
        body: data,
      }),
      transformResponse: (response: ApiResponse<CafeTable>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        "CafeTables",
        { type: "CafeTables", id },
      ], // Làm mới danh sách và bàn cụ thể
    }),

    // Xóa bàn
    deleteTable: builder.mutation<{ message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "DELETE",
      }),
      transformResponse: (response: ApiResponse<null>) => ({
        message: response.message,
      }),
      invalidatesTags: ["CafeTables"], // Làm mới danh sách bàn sau khi xóa
    }),
  }),
});

export const {
  useGetAllTablesQuery,
  useGetTableByIdQuery,
  useCreateTableMutation,
  useUpdateTableMutation,
  useDeleteTableMutation,
} = cafeTableServices;
