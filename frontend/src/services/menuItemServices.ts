/* eslint-disable @typescript-eslint/no-explicit-any */
import { baseRestApi } from "./baseRestApi";
import { MenuItem } from "@/interfaces/menuItem";
import { ApiResponse } from "@/interfaces/apiResponse";

const entity = "menu-items";

export const menuItemServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    // Lấy danh sách tất cả món
    getAllMenuItems: builder.query<{ data: MenuItem[]; message: string }, void>(
      {
        query: () => ({
          url: `${entity}`,
          method: "GET",
        }),
        transformResponse: (response: ApiResponse<MenuItem[]>) => ({
          data: response.data,
          message: response.message,
        }),
        providesTags: ["MenuItems"], // Để invalidate cache khi cần
      }
    ),

    // Lấy món theo ID
    getMenuItemById: builder.query<{ data: MenuItem; message: string }, string>(
      {
        query: (id) => ({
          url: `${entity}/${id}`,
          method: "GET",
        }),
        transformResponse: (response: ApiResponse<MenuItem>) => ({
          data: response.data,
          message: response.message,
        }),
        providesTags: (_result, _error, id) => [{ type: "MenuItems", id }], // Cache theo ID
      }
    ),

    // Tạo món mới
    createMenuItem: builder.mutation<
      { data: MenuItem; message: string },
      { menuItem: any; image?: File }
    >({
      query: ({ menuItem, image }) => {
        const formData = new FormData();
        for (const key in menuItem) {
          const value = menuItem[key];
          if (value !== null && value !== undefined) {
            formData.append(key, value.toString());
          }
        }
        if (image) {
          formData.append("image", image);
        }
        return {
          url: `${entity}`,
          method: "POST",
          body: formData,
        };
      },
      transformResponse: (response: ApiResponse<MenuItem>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["MenuItems"], // Làm mới danh sách món sau khi tạo
    }),

    // Cập nhật thông tin món
    updateMenuItem: builder.mutation<
      { data: MenuItem; message: string },
      { id: string; menuItem: any; image?: File }
    >({
      query: ({ id, menuItem, image }) => {
        const formData = new FormData();
        for (const key in menuItem) {
          const value = menuItem[key];
          if (value !== null && value !== undefined) {
            formData.append(key, value.toString());
          }
        }
        if (image) {
          formData.append("image", image);
        }
        return {
          url: `${entity}/${id}`,
          method: "PUT",
          body: formData,
        };
      },
      transformResponse: (response: ApiResponse<MenuItem>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        "MenuItems",
        { type: "MenuItems", id },
      ], // Làm mới danh sách và món cụ thể
    }),

    // Xóa món
    deleteMenuItem: builder.mutation<{ message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "DELETE",
      }),
      transformResponse: (response: ApiResponse<null>) => ({
        message: response.message,
      }),
      invalidatesTags: ["MenuItems"], // Làm mới danh sách món sau khi xóa
    }),
  }),
});

export const {
  useGetAllMenuItemsQuery,
  useGetMenuItemByIdQuery,
  useCreateMenuItemMutation,
  useUpdateMenuItemMutation,
  useDeleteMenuItemMutation,
} = menuItemServices;
