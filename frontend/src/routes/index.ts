import AdminLayout from "@/layouts/AdminLayout";
import AuthLayout from "@/layouts/AuthLayout";
import UserLayout from "@/layouts/UserLayout";
import DashBoard from "@/pages/Admin/DashBoard";
import TableManagentsPage from "@/pages/Admin/TableManagentsPage";
import LoginPage from "@/pages/Auth/LoginPage";
import RegisterPage from "@/pages/Auth/RegisterPage";
import HomePage from "@/pages/HomePage";

export const publicRoutes = [
  { path: "/", component: HomePage, layout: UserLayout },
  { path: "/login", component: LoginPage, layout: AuthLayout },
  { path: "/register", component: RegisterPage, layout: AuthLayout },
  { path: "/admin/dashboard", component: DashBoard, layout: AdminLayout },
  {
    path: "/admin/table",
    component: TableManagentsPage,
    layout: AdminLayout,
  },
];
export const privateRoutes = [
  //   { path: "/profile/:id", component: ProfilePage, layout: AuthLayout },
  //   { path: "/messenger", component: MessengerPage, layout: MessengerLayout },
  //   { path: "/search", component: SearchResult, layout: MainSidebarLayout },
];
