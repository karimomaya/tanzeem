
import { Button } from "@/components/ui/button";
import { useLocation } from "react-router-dom";

export function Navbar() {
  const location = useLocation();
  
  // Get the title based on the current route
  const getTitle = () => {
    switch (location.pathname) {
      case "/":
        return "لوحة التحكم";
      case "/products":
        return "المنتجات";
      case "/purchases":
        return "المشتريات";
      case "/sales":
        return "المبيعات";
      case "/expenses":
        return "النفقات";
      case "/users":
        return "المستخدمين";
      default:
        return "تنظيم";
    }
  };

  return (
    <header className="border-b bg-white sticky top-0 z-10">
      <div className="flex h-16 items-center px-6">
        <h2 className="text-lg font-semibold">{getTitle()}</h2>
        <div className="mr-auto flex items-center gap-2">
          <Button size="sm" variant="outline">
            المساعدة
          </Button>
          <Button size="sm">
            الإعدادات
          </Button>
        </div>
      </div>
    </header>
  );
}
