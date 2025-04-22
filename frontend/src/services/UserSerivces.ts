import { baseRestApi } from "./baseRestApi";
import { UserUpdate, User, UserRequest } from "@/interfaces/user";
import { ApiResponse } from "@/interfaces/apiResponse";
// import { RootState } from '@/stores';
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
          formData.append("avatar", file);
        }
        Object.entries(data).forEach(([key, value]) => {
          if (value !== undefined) {
            formData.append(key, value);
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
          // const state = getState() as RootState;
          // const accessToken = state.auth.token?.accessToken || '';
          // const refreshToken = state.auth.token?.refreshToken || '';

          dispatch(
            setUser({
              user: data.data,
            })
          );
        } catch (error) {
          console.error("Update user failed:", error);
        }
      },
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
        body: data,
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["Users"],
    }),

    updateUser: builder.mutation<
      { data: User; message: string },
      { data: UserRequest }
    >({
      query: ({ data }) => ({
        url: `${entity}`,
        method: "PUT",
        body: data,
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
      invalidatesTags: ["Users"], // Làm mới danh sách và bàn cụ thể
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
