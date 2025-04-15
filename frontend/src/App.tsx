import "./App.css";
import { Route, Routes, Navigate } from "react-router-dom";
import { publicRoutes, privateRoutes, adminRoutes } from "./routes";

function App() {
  const authInfo = JSON.parse(localStorage.getItem("authInfo") || "{}");
  const user = authInfo?.user;
  const isAdmin = user?.role === "ADMIN";

  // Helper để render routes
  const renderRoutes = (routes: any[]) =>
    routes.map(({ path, component: Component, layout: Layout }) => (
      <Route
        key={path}
        path={path}
        element={
          Layout ? (
            <Layout>
              <Component />
            </Layout>
          ) : (
            <Component />
          )
        }
      />
    ));

  return (
    <div>
      <Routes>
        
        {/* Public Routes */}
        {renderRoutes(publicRoutes)}

        {/* Private Routes (chỉ khi đã login) */}
        {user && renderRoutes(privateRoutes)}

        {/* Admin Routes (chỉ khi là ADMIN) */}
        {isAdmin && renderRoutes(adminRoutes)}

        {/* Fallback Route */}
        <Route
          path="*"
          element={<Navigate to={user ? "/profile" : "/login"} replace />}
        />
      </Routes>
    </div>
  );
}

export default App;