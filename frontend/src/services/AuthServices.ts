/* eslint-disable @typescript-eslint/no-explicit-any */
import { baseRestApi } from "./baseRestApi";
import { logout, setCredentials, setUser } from "@/stores/authSlice";
import { RootState, store } from "@/stores";
import { UserCredentials, User } from "@/interfaces/user";
import { ApiResponse } from "@/interfaces/apiResponse";
import { userServices } from "./UserSerivces";

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}
const entity = "auth";

export const authServices = baseRestApi.injectEndpoints({
  endpoints: (builder) => ({
    login: builder.mutation<
      { data: AuthResponse; message: string; status: number },
      UserCredentials
    >({
      query: (credentials) => ({
        url: `${entity}/login`,
        method: "POST",
        body: credentials,
      }),
      transformResponse: (response: ApiResponse<AuthResponse>) => ({
        data: response.data,
        message: response.message,
        status: response.status,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        try {
          const { data: authData } = await queryFulfilled;
          console.log("Login response:", authData);

          dispatch(
            setCredentials({
              accessToken: authData.data.accessToken,
              refreshToken: authData.data.refreshToken,
            } as { accessToken: string; refreshToken: string })
          );
          // Verify state update
          const state = store.getState() as RootState;
          if (!state.auth.token?.accessToken) {
            throw new Error("Failed to update tokens in state");
          }
          const meResult = await dispatch(
            userServices.endpoints.getMe.initiate(undefined)
          ).unwrap();

          dispatch(
            setUser({
              user: meResult.data,
            })
          );
        } catch (error) {
          console.error("Login failed:", error);
        }
      },
      invalidatesTags: ["Users"],
    }),
    register: builder.mutation<
      { data: User; message: string; status: number },
      UserCredentials
    >({
      query: (credentials) => ({
        url: `${entity}/register`,
        method: "POST",
        body: credentials,
      }),
      transformResponse: (response: ApiResponse<User>) => ({
        data: response.data,
        message: response.message,
        status: response.status,
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
          console.error("Register failed:", error);
        }
      },
    }),
    refreshToken: builder.mutation<
      { data: AuthResponse; message: string; status: number },
      void
    >({
      query: () => ({
        url: `${entity}/refresh`,
        method: "POST",
        body: {
          refresh: (store.getState() as RootState).auth.token?.refreshToken,
        },
      }),
      transformResponse: (response: ApiResponse<AuthResponse>) => ({
        data: response.data,
        message: response.message,
        status: response.status,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          dispatch(
            setCredentials({
              accessToken: data.data.accessToken,
              refreshToken: data.data.refreshToken,
            } as { accessToken: string; refreshToken: string })
          );
        } catch (error) {
          console.error("Refresh token failed:", error);
        }
      },
    }),
    logout: builder.mutation<
      { data: any; message: string; status: number },
      void
    >({
      query: () => ({
        url: `${entity}/logout`,
        method: "POST",
        body: (store.getState() as RootState).auth.token?.refreshToken,
      }),
      transformResponse: (response: ApiResponse<any>) => ({
        data: response.data,
        message: response.message,
        status: response.status,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(logout());
          dispatch(baseRestApi.util.resetApiState());
        } catch (error) {
          console.error("Logout failed:", error);
        }
      },
      invalidatesTags: ["Users"],
    }),
  }),
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useRefreshTokenMutation,
  useLogoutMutation,
} = authServices;
