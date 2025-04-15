import HomePage from "@/pages/HomePage";

export const publicRoutes = [
  { path: "/", component: HomePage },
  // { path: "/login", component: LoginPage, layout: AuthLayout },
  // { path: "/register", component: RegisterPage, layout: AuthLayout },
];

export const privateRoutes = [
  // { path: "/profile", component: ProfilePage, layout: UserLayout },
];

export const adminRoutes = [
  // {
  //   path: "/admin",
  //   component: UserManagement,
  //   layout: SidebarLayout,
  // },
];
