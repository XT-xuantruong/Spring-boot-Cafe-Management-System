import { baseRestApi } from "./baseRestApi";
import { UserUpdate, User } from "@/interfaces/user";
import { ApiResponse } from "@/interfaces/apiResponse";
// import { RootState } from '@/stores';
import { setUser } from "@/stores/authSlice";

const entity = "users";

export const userServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    getMe: builder.query<{ data: User; message: string }, void>({
      query: () => ({
        url: `${entity}/me`,
        method: "GET",
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
      }),
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
    }),
    updateUser: builder.mutation<
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
          url: `${entity}/me`,
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
  }),
});
export const { useGetMeQuery, useUpdateUserMutation, useGetByIdQuery } =
  userServices;
