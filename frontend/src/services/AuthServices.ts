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
          authServices.endpoints.sendOtp.initiate(null);
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
    sendOtp: builder.mutation<
      { data: any; message: string; status: number },
      null
    >({
      query: () => ({
        url: `${entity}/otp/send`,
        method: "POST",
        body: { email: (store.getState() as RootState).auth.user?.email },
      }),
      transformResponse: (response: ApiResponse<any>) => ({
        data: response.data,
        message: response.message,
        status: response.status,
      }),
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
        } catch (error) {
          console.error("Send OTP failed:", error);
        }
      },
    }),
    verifyOtp: builder.mutation<
      { data: any; message: string; status: number },
      string
    >({
      query: (otp) => ({
        url: `${entity}/otp/verify`,
        method: "POST",
        body: { email: (store.getState() as RootState).auth.user?.email, otp },
      }),
      transformResponse: (response: ApiResponse<any>) => ({
        data: response.data,
        message: response.message,
        status: response.status,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          dispatch(
            setUser({
              user: data.data.user,
            })
          );
        } catch (error) {
          console.error("Verify OTP failed:", error);
        }
      },
    }),
    loginGoogle: builder.mutation<
      { data: any; message: string; status: number },
      any
    >({
      query: (token) => ({
        url: `${entity}/google-login`,
        method: "POST",
        body: token,
      }),
      transformResponse: (response: ApiResponse<any>) => ({
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
            })
          );
          const meResult = await dispatch(
            userServices.endpoints.getMe.initiate(undefined)
          ).unwrap();
          dispatch(
            setUser({
              user: meResult.data,
            })
          );
        } catch (error) {
          console.error("Login with Google failed:", error);
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
        body: {
          refreshToken: (store.getState() as RootState).auth.token
            ?.refreshToken,
        },
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
        } catch (error) {
          console.error("Logout failed:", error);
        }
      },
    }),
  }),
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useRefreshTokenMutation,
  useLogoutMutation,
} = authServices;
