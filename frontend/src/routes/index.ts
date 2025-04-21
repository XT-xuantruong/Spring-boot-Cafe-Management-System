import UserLayout from "@/layouts/UserLayout";
import HomePage from "@/pages/HomePage";

export const publicRoutes = [
  //   { path: "/login", component: LoginPage, layout: AuthLayout },
  //   { path: "/register", component: RegisterPage, layout: AuthLayout },
  //   { path: "/otp", component: OtpFormPage, layout: AuthLayout },
  { path: "/", component: HomePage, layout: UserLayout },
];
export const privateRoutes = [
  //   { path: "/profile/:id", component: ProfilePage, layout: AuthLayout },
  //   { path: "/messenger", component: MessengerPage, layout: MessengerLayout },
  //   { path: "/search", component: SearchResult, layout: MainSidebarLayout },
];
