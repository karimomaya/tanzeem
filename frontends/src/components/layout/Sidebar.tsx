
import { NavLink } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Sidebar as SidebarContainer,
  SidebarContent,
  SidebarHeader,
  SidebarFooter,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { cn } from "@/lib/utils";
import { useState } from "react";
import {
  Menu,
  LayoutDashboard,
  Package,
  ShoppingBag,
  CreditCard,
  Receipt,
  Users,
  LogOut,
} from "lucide-react";

const navItems = [
  {
    title: "لوحة التحكم",
    path: "/",
    icon: LayoutDashboard,
  },
  {
    title: "المنتجات",
    path: "/products",
    icon: Package,
  },
  {
    title: "المشتريات",
    path: "/purchases",
    icon: ShoppingBag,
  },
  {
    title: "المبيعات",
    path: "/sales",
    icon: CreditCard,
  },
  {
    title: "النفقات",
    path: "/expenses",
    icon: Receipt,
  },
  {
    title: "المستخدمين",
    path: "/users",
    icon: Users,
  },
];

export function Sidebar() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  return (
    <>
      <div className="md:hidden fixed top-4 right-4 z-40">
        <Button
          variant="outline"
          size="icon"
          onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
        >
          <Menu className="h-5 w-5" />
        </Button>
      </div>

      <SidebarContainer 
        className={cn(
          "fixed inset-y-0 right-0 z-30 w-64 border-l bg-white transition-transform md:translate-x-0",
          isMobileMenuOpen ? "translate-x-0" : "translate-x-full md:translate-x-0"
        )}
      >
        <SidebarHeader className="flex h-16 items-center border-b px-6">
          <h2 className="text-lg font-bold text-gray-900">تنظيم</h2>
          <div className="hidden md:flex ml-auto">
            <SidebarTrigger />
          </div>
        </SidebarHeader>
        
        <SidebarContent>
          <nav className="flex flex-col gap-1 p-2">
            {navItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) => cn(
                  "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                  isActive 
                    ? "bg-primary text-primary-foreground" 
                    : "hover:bg-muted text-gray-700 hover:text-gray-900"
                )}
              >
                <item.icon className="h-5 w-5 flip-x" />
                <span>{item.title}</span>
              </NavLink>
            ))}
          </nav>
        </SidebarContent>
        
        <SidebarFooter className="p-2">
          <Button variant="outline" className="w-full justify-start">
            <LogOut className="h-5 w-5 ml-2 flip-x" />
            <span>تسجيل خروج</span>
          </Button>
        </SidebarFooter>
      </SidebarContainer>
    </>
  );
}
