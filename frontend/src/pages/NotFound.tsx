
import { useLocation } from "react-router-dom";
import { useEffect } from "react";
import { Button } from "@/components/ui/button";

const NotFound = () => {
  const location = useLocation();

  useEffect(() => {
    console.error(
      "404 Error: User attempted to access non-existent route:",
      location.pathname
    );
  }, [location.pathname]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-tanzeem-background">
      <div className="text-center">
        <h1 className="text-4xl font-bold mb-4">٤٠٤</h1>
        <p className="text-xl text-muted-foreground mb-4">عذراً، الصفحة غير موجودة</p>
        <Button asChild>
          <a href="/">العودة إلى الرئيسية</a>
        </Button>
      </div>
    </div>
  );
};

export default NotFound;
