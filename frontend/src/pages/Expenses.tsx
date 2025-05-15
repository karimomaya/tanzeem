
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Edit, Trash2 } from "lucide-react";
import { useState } from "react";
import { Badge } from "@/components/ui/badge";

interface Expense {
  id: number;
  description: string;
  category: string;
  date: string;
  amount: number;
}

const Expenses = () => {
  const [searchTerm, setSearchTerm] = useState("");
  
  // Mock expenses data
  const expenses: Expense[] = [
    { id: 1, description: "إيجار المكتب", category: "إيجارات", date: "١٥ مايو ٢٠٢٥", amount: 3000 },
    { id: 2, description: "فاتورة الكهرباء", category: "مرافق", date: "١٤ مايو ٢٠٢٥", amount: 450 },
    { id: 3, description: "فاتورة الإنترنت", category: "مرافق", date: "١٤ مايو ٢٠٢٥", amount: 300 },
    { id: 4, description: "رواتب الموظفين", category: "رواتب", date: "١٣ مايو ٢٠٢٥", amount: 12000 },
    { id: 5, description: "صيانة المعدات", category: "صيانة", date: "١٢ مايو ٢٠٢٥", amount: 800 },
    { id: 6, description: "تسويق وإعلان", category: "تسويق", date: "١١ مايو ٢٠٢٥", amount: 1500 },
  ];
  
  // Filter expenses based on search term
  const filteredExpenses = expenses.filter(
    (expense) =>
      expense.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      expense.category.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Calculate total expenses
  const totalExpenses = filteredExpenses.reduce((sum, expense) => sum + expense.amount, 0);

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
          إضافة نفقة جديدة
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <Card className="card-stats">
          <div className="card-header">
            <CardTitle className="card-title">إجمالي النفقات</CardTitle>
          </div>
          <CardContent className="card-content">
            {totalExpenses} ر.س
          </CardContent>
          <CardFooter className="card-footer">
            <p>للفترة المحددة</p>
          </CardFooter>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>قائمة النفقات</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-right">
              <thead>
                <tr className="border-b">
                  <th className="pb-2 pr-4 text-sm font-medium">الوصف</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الفئة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">التاريخ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">المبلغ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الإجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredExpenses.map((expense) => (
                  <tr key={expense.id} className="border-b">
                    <td className="py-3 pr-4">{expense.description}</td>
                    <td className="py-3 pr-4">
                      <Badge variant="outline">{expense.category}</Badge>
                    </td>
                    <td className="py-3 pr-4">{expense.date}</td>
                    <td className="py-3 pr-4">{expense.amount} ر.س</td>
                    <td className="py-3 pr-4">
                      <div className="flex space-x-2">
                        <Button variant="ghost" size="icon" className="ml-2">
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="icon" className="text-red-500">
                          <Trash2 className="h-4 w-4" />
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
            إجمالي السجلات: {filteredExpenses.length}
          </div>
        </CardFooter>
      </Card>
    </div>
  );
};

export default Expenses;
