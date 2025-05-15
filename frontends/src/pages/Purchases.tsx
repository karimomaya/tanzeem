
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Download, Eye } from "lucide-react";
import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

interface Purchase {
  id: number;
  orderNumber: string;
  supplier: string;
  date: string;
  amount: number;
  items: number;
  status: "received" | "pending" | "canceled";
}

const Purchases = () => {
  const [searchTerm, setSearchTerm] = useState("");
  
  // Mock purchase data
  const purchases: Purchase[] = [
    { id: 1, orderNumber: "PO-2025-001", supplier: "شركة التقنية العالمية", date: "١٥ مايو ٢٠٢٥", amount: 12500, items: 5, status: "received" },
    { id: 2, orderNumber: "PO-2025-002", supplier: "مؤسسة الخليج للتجارة", date: "١٤ مايو ٢٠٢٥", amount: 8750, items: 3, status: "received" },
    { id: 3, orderNumber: "PO-2025-003", supplier: "شركة المستقبل", date: "١٤ مايو ٢٠٢٥", amount: 15000, items: 7, status: "pending" },
    { id: 4, orderNumber: "PO-2025-004", supplier: "شركة التقنية العالمية", date: "١٣ مايو ٢٠٢٥", amount: 6300, items: 2, status: "pending" },
    { id: 5, orderNumber: "PO-2025-005", supplier: "مؤسسة النور للإلكترونيات", date: "١٢ مايو ٢٠٢٥", amount: 9200, items: 4, status: "canceled" },
  ];
  
  // Filter purchases based on search term
  const filteredPurchases = purchases.filter(
    (purchase) =>
      purchase.orderNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      purchase.supplier.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "received":
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">تم الاستلام</Badge>;
      case "pending":
        return <Badge className="bg-amber-100 text-amber-800 hover:bg-amber-100">قيد التنفيذ</Badge>;
      case "canceled":
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-100">ملغي</Badge>;
      default:
        return null;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="w-full sm:w-64">
          <Input
            placeholder="بحث..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full"
          />
        </div>
        <Button>
          <Plus className="ml-2 h-4 w-4" />
          إنشاء طلب شراء
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>طلبات الشراء</CardTitle>
            <Tabs defaultValue="all" className="w-[300px]">
              <TabsList>
                <TabsTrigger value="all">الكل</TabsTrigger>
                <TabsTrigger value="received">تم الاستلام</TabsTrigger>
                <TabsTrigger value="pending">قيد التنفيذ</TabsTrigger>
              </TabsList>
            </Tabs>
          </div>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-right">
              <thead>
                <tr className="border-b">
                  <th className="pb-2 pr-4 text-sm font-medium">رقم الطلب</th>
                  <th className="pb-2 pr-4 text-sm font-medium">المورد</th>
                  <th className="pb-2 pr-4 text-sm font-medium">التاريخ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">المبلغ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">العناصر</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الحالة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الإجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredPurchases.map((purchase) => (
                  <tr key={purchase.id} className="border-b">
                    <td className="py-3 pr-4">{purchase.orderNumber}</td>
                    <td className="py-3 pr-4">{purchase.supplier}</td>
                    <td className="py-3 pr-4">{purchase.date}</td>
                    <td className="py-3 pr-4">{purchase.amount} ر.س</td>
                    <td className="py-3 pr-4">{purchase.items}</td>
                    <td className="py-3 pr-4">
                      {getStatusBadge(purchase.status)}
                    </td>
                    <td className="py-3 pr-4">
                      <div className="flex space-x-2">
                        <Button variant="ghost" size="icon" className="ml-2">
                          <Eye className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="icon">
                          <Download className="h-4 w-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
        <CardFooter className="flex justify-between border-t p-4">
          <div className="text-sm text-muted-foreground">
            إجمالي الطلبات: {filteredPurchases.length}
          </div>
        </CardFooter>
      </Card>
    </div>
  );
};

export default Purchases;
