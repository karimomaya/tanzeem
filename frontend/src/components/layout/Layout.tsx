
import { Outlet } from "react-router-dom";
import { Sidebar } from "./Sidebar";
import { Navbar } from "./Navbar";

const Layout = () => {
  return (
    <div className="min-h-screen w-full flex flex-col md:flex-row">
      <Sidebar />
      <main className="flex-1">
        <Navbar />
        <div className="p-4 md:p-6">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default Layout;
