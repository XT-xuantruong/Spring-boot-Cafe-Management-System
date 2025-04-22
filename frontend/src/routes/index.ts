import AdminLayout from "@/layouts/AdminLayout";
import AuthLayout from "@/layouts/AuthLayout";
import UserLayout from "@/layouts/UserLayout";
import DashBoard from "@/pages/Admin/DashBoard";
import MenuManagementPage from "@/pages/Admin/MenuManagementPage";
import TableManagentsPage from "@/pages/Admin/TableManagentsPage";
import UserManagementPage from "@/pages/Admin/UserManagementPage";
import LoginPage from "@/pages/Auth/LoginPage";
import RegisterPage from "@/pages/Auth/RegisterPage";
import HomePage from "@/pages/HomePage";

export const publicRoutes = [
  { path: "/", component: HomePage, layout: UserLayout },
  { path: "/login", component: LoginPage, layout: AuthLayout },
  { path: "/register", component: RegisterPage, layout: AuthLayout },
];
export const privateRoutes = [
  { path: "/admin/dashboard", component: DashBoard, layout: AdminLayout },
  {
    path: "/admin/table",
    component: TableManagentsPage,
    layout: AdminLayout,
  },
  {
    path: "/admin/menu",
    component: MenuManagementPage,
    layout: AdminLayout,
  },
  {
    path: "/admin/user",
    component: UserManagementPage,
    layout: AdminLayout,
  },
  //   { path: "/profile/:id", component: ProfilePage, layout: AuthLayout },
  //   { path: "/messenger", component: MessengerPage, layout: MessengerLayout },
  //   { path: "/search", component: SearchResult, layout: MainSidebarLayout },
];
