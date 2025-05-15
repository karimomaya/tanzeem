
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

interface Sale {
  id: number;
  invoiceNumber: string;
  customer: string;
  date: string;
  amount: number;
  discount: number;
  status: "paid" | "pending" | "cancelled";
}

const Sales = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  
  // Mock sales data
  const sales: Sale[] = [
    { id: 1, invoiceNumber: "INV-2025-001", customer: "شركة الفيصل", date: "١٥ مايو ٢٠٢٥", amount: 3500, discount: 0, status: "paid" },
    { id: 2, invoiceNumber: "INV-2025-002", customer: "مؤسسة النور", date: "١٤ مايو ٢٠٢٥", amount: 2800, discount: 200, status: "paid" },
    { id: 3, invoiceNumber: "INV-2025-003", customer: "شركة الأمان", date: "١٤ مايو ٢٠٢٥", amount: 4200, discount: 0, status: "pending" },
    { id: 4, invoiceNumber: "INV-2025-004", customer: "مكتبة المعرفة", date: "١٣ مايو ٢٠٢٥", amount: 1950, discount: 50, status: "paid" },
    { id: 5, invoiceNumber: "INV-2025-005", customer: "شركة الراية", date: "١٣ مايو ٢٠٢٥", amount: 5600, discount: 400, status: "cancelled" },
  ];
  
  // Filter sales based on search term and status
  const filteredSales = sales.filter(
    (sale) =>
      (sale.invoiceNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      sale.customer.toLowerCase().includes(searchTerm.toLowerCase())) &&
      (statusFilter === "all" || sale.status === statusFilter)
  );

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "paid":
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">تم الدفع</Badge>;
      case "pending":
        return <Badge className="bg-amber-100 text-amber-800 hover:bg-amber-100">معلق</Badge>;
      case "cancelled":
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-100">ملغي</Badge>;
      default:
        return null;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex flex-col sm:flex-row gap-2 w-full sm:w-auto">
          <Input
            placeholder="بحث..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full sm:w-64"
          />
          <Select 
            value={statusFilter} 
            onValueChange={setStatusFilter}
          >
            <SelectTrigger className="w-full sm:w-40">
              <SelectValue placeholder="الحالة" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">جميع الحالات</SelectItem>
              <SelectItem value="paid">تم الدفع</SelectItem>
              <SelectItem value="pending">معلق</SelectItem>
              <SelectItem value="cancelled">ملغي</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <Button>
          <Plus className="ml-2 h-4 w-4" />
          إنشاء فاتورة مبيعات
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>فواتير المبيعات</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-right">
              <thead>
                <tr className="border-b">
                  <th className="pb-2 pr-4 text-sm font-medium">رقم الفاتورة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">العميل</th>
                  <th className="pb-2 pr-4 text-sm font-medium">التاريخ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">المبلغ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الخصم</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الحالة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الإجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredSales.map((sale) => (
                  <tr key={sale.id} className="border-b">
                    <td className="py-3 pr-4">{sale.invoiceNumber}</td>
                    <td className="py-3 pr-4">{sale.customer}</td>
                    <td className="py-3 pr-4">{sale.date}</td>
                    <td className="py-3 pr-4">{sale.amount} ر.س</td>
                    <td className="py-3 pr-4">{sale.discount > 0 ? `${sale.discount} ر.س` : "-"}</td>
                    <td className="py-3 pr-4">
                      {getStatusBadge(sale.status)}
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
            إجمالي الفواتير: {filteredSales.length}
          </div>
        </CardFooter>
      </Card>
    </div>
  );
};

export default Sales;
