import { baseRestApi } from "./baseRestApi";
import { UserUpdate, User, UserRequest } from "@/interfaces/user";
import { ApiResponse } from "@/interfaces/apiResponse";
import { setUser } from "@/stores/authSlice";

const entity = "users";

export const userServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    getMe: builder.query<{ data: User; message: string }, void>({
      query: () => ({
        url: `${entity}/profile`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["Users"],
    }),
    updateProfile: builder.mutation<
      { data: User; message: string },
      { data: UserUpdate; file?: File }
    >({
      query: ({ data, file }) => {
        const formData = new FormData();
        if (file) {
          formData.append("avatarUrl", file); // Đổi từ "avatar" sang "avatarUrl" để khớp với backend
        }
        Object.entries(data).forEach(([key, value]) => {
          if (value !== undefined && value !== null) {
            formData.append(key, value.toString());
          }
        });

        return {
          url: `${entity}/profile`,
          method: "PUT",
          body: formData,
        };
      },
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          dispatch(
            setUser({
              user: data.data,
            })
          );
        } catch (error) {
          console.error("Update profile failed:", error);
        }
      },
      invalidatesTags: ["Users"],
    }),
    getAllUsers: builder.query<{ data: User[]; message: string }, void>({
      query: () => ({
        url: `${entity}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<User[]>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: ["Users"],
    }),
    getById: builder.query<{ data: User; message: string }, string>({
      query: (id = "") => ({
        url: `${entity}/${id}`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      providesTags: (result, error, id) => [{ type: "Users", id }],
    }),
    createUser: builder.mutation<{ data: User; message: string }, UserRequest>({
      query: (data) => ({
        url: `${entity}`,
        method: "POST",
        body: {
          email: data.email,
          password: data.password,
          name: data.name,
          phone: data.phone ?? null,
          address: data.address ?? null,
          avatarUrl: data.avatarUrl ?? null,
          role: data.role,
        },
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["Users"],
    }),
    updateUser: builder.mutation<
      { data: User; message: string },
      { id: string; data: UserUpdate }
    >({
      query: ({ id, data }) => ({
        url: `${entity}/${id}`,
        method: "PUT",
        body: {
          name: data.name,
          phone: data.phone ?? null,
          address: data.address ?? null,
          avatarUrl: data.avatarUrl ?? null,
          role: data.role,
          password: data.password,
        },
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["Users"],
    }),
    deleteUser: builder.mutation<{ message: string }, string>({
      query: (id) => ({
        url: `${entity}/${id}`,
        method: "DELETE",
      }),
      transformResponse: (response: ApiResponse<null>) => ({
        message: response.message,
      }),
      invalidatesTags: ["Users"],
    }),
  }),
});

export const {
  useGetMeQuery,
  useUpdateProfileMutation,
  useCreateUserMutation,
  useDeleteUserMutation,
  useUpdateUserMutation,
  useGetAllUsersQuery,
  useGetByIdQuery,
} = userServices;
