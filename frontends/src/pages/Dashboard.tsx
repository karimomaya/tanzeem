
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ArrowUpRight, ArrowDownRight, AlertTriangle } from "lucide-react";

const Dashboard = () => {
  return (
    <div className="space-y-6">
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card className="card-stats">
          <div className="card-header">
            <CardTitle className="card-title">إجمالي المبيعات اليوم</CardTitle>
            <ArrowUpRight className="h-4 w-4 text-green-500" />
          </div>
          <CardContent className="card-content">
            ١٢,٥٠٠ ر.س
          </CardContent>
          <CardFooter className="card-footer">
            <p>زيادة بنسبة ١٠٪ عن أمس</p>
          </CardFooter>
        </Card>
        <Card className="card-stats">
          <div className="card-header">
            <CardTitle className="card-title">إجمالي النفقات</CardTitle>
            <ArrowDownRight className="h-4 w-4 text-red-500" />
          </div>
          <CardContent className="card-content">
            ٤,٧٥٠ ر.س
          </CardContent>
          <CardFooter className="card-footer">
            <p>انخفاض بنسبة ٥٪ عن أمس</p>
          </CardFooter>
        </Card>
        <Card className="card-stats">
          <div className="card-header">
            <CardTitle className="card-title">صافي الربح</CardTitle>
            <ArrowUpRight className="h-4 w-4 text-green-500" />
          </div>
          <CardContent className="card-content">
            ٧,٧٥٠ ر.س
          </CardContent>
          <CardFooter className="card-footer">
            <p>زيادة بنسبة ١٥٪ عن أمس</p>
          </CardFooter>
        </Card>
        <Card className="card-stats">
          <div className="card-header">
            <CardTitle className="card-title">عدد المبيعات</CardTitle>
            <ArrowUpRight className="h-4 w-4 text-green-500" />
          </div>
          <CardContent className="card-content">
            ٢٨
          </CardContent>
          <CardFooter className="card-footer">
            <p>زيادة بنسبة ٨٪ عن أمس</p>
          </CardFooter>
        </Card>
      </div>
      
      <div className="grid gap-4 md:grid-cols-2">
        <Card className="dashboard-card">
          <CardHeader className="pb-3">
            <CardTitle>المنتجات الأكثر مبيعاً</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <div className="space-y-4 px-6">
              <div>
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium">ايفون ١٥ برو</span>
                  <span className="text-sm text-muted-foreground">٨٥٪</span>
                </div>
                <Progress value={85} className="h-2" />
              </div>
              <div>
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium">سامسونج جالكسي اس ٢٣</span>
                  <span className="text-sm text-muted-foreground">٧٢٪</span>
                </div>
                <Progress value={72} className="h-2" />
              </div>
              <div>
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium">ماك بوك برو</span>
                  <span className="text-sm text-muted-foreground">٦٠٪</span>
                </div>
                <Progress value={60} className="h-2" />
              </div>
              <div>
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium">ايباد برو</span>
                  <span className="text-sm text-muted-foreground">٤٨٪</span>
                </div>
                <Progress value={48} className="h-2" />
              </div>
              <div>
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium">سماعات ايربودز</span>
                  <span className="text-sm text-muted-foreground">٣٥٪</span>
                </div>
                <Progress value={35} className="h-2" />
              </div>
            </div>
          </CardContent>
          <CardFooter className="border-t p-4">
            <p className="text-xs text-muted-foreground">تم التحديث قبل ٥ دقائق</p>
          </CardFooter>
        </Card>
        
        <Card className="dashboard-card">
          <CardHeader className="pb-3">
            <CardTitle>تنبيهات المخزون</CardTitle>
          </CardHeader>
          <CardContent className="pb-2">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <AlertTriangle className="h-5 w-5 text-amber-500" />
                  <span className="font-medium">سامسونج جالكسي اس ٢٣</span>
                </div>
                <span className="text-sm text-red-500">متبقي ٣ قطع</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <AlertTriangle className="h-5 w-5 text-amber-500" />
                  <span className="font-medium">سماعات ايربودز</span>
                </div>
                <span className="text-sm text-red-500">متبقي ٥ قطع</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <AlertTriangle className="h-5 w-5 text-amber-500" />
                  <span className="font-medium">ساعة ابل</span>
                </div>
                <span className="text-sm text-red-500">متبقي ٤ قطع</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <AlertTriangle className="h-5 w-5 text-amber-500" />
                  <span className="font-medium">جوال هواوي</span>
                </div>
                <span className="text-sm text-red-500">متبقي ٢ قطعة</span>
              </div>
            </div>
          </CardContent>
          <CardFooter className="border-t p-4">
            <p className="text-xs text-muted-foreground">يجب طلب المزيد من المخزون</p>
          </CardFooter>
        </Card>
      </div>

      <Card className="dashboard-card">
        <CardHeader className="pb-3">
          <div className="flex items-center justify-between">
            <CardTitle>أحدث المعاملات</CardTitle>
            <Tabs defaultValue="sales">
              <TabsList className="grid w-52 grid-cols-2">
                <TabsTrigger value="sales">المبيعات</TabsTrigger>
                <TabsTrigger value="purchases">المشتريات</TabsTrigger>
              </TabsList>
            </Tabs>
          </div>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-right">
              <thead>
                <tr className="border-b">
                  <th className="pb-2 pr-4 text-sm font-medium">رقم المعاملة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">التاريخ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">العميل</th>
                  <th className="pb-2 pr-4 text-sm font-medium">المبلغ</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الحالة</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b">
                  <td className="py-3 pr-4">#١٢٣٤</td>
                  <td className="py-3 pr-4">١٥ مايو ٢٠٢٥</td>
                  <td className="py-3 pr-4">شركة الفيصل</td>
                  <td className="py-3 pr-4">٣,٥٠٠ ر.س</td>
                  <td className="py-3 pr-4"><span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">تم الدفع</span></td>
                </tr>
                <tr className="border-b">
                  <td className="py-3 pr-4">#١٢٣٣</td>
                  <td className="py-3 pr-4">١٤ مايو ٢٠٢٥</td>
                  <td className="py-3 pr-4">مؤسسة النور</td>
                  <td className="py-3 pr-4">٢,٨٠٠ ر.س</td>
                  <td className="py-3 pr-4"><span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">تم الدفع</span></td>
                </tr>
                <tr className="border-b">
                  <td className="py-3 pr-4">#١٢٣٢</td>
                  <td className="py-3 pr-4">١٤ مايو ٢٠٢٥</td>
                  <td className="py-3 pr-4">شركة الأمان</td>
                  <td className="py-3 pr-4">٤,٢٠٠ ر.س</td>
                  <td className="py-3 pr-4"><span className="bg-amber-100 text-amber-800 text-xs px-2 py-1 rounded">معلق</span></td>
                </tr>
                <tr className="border-b">
                  <td className="py-3 pr-4">#١٢٣١</td>
                  <td className="py-3 pr-4">١٣ مايو ٢٠٢٥</td>
                  <td className="py-3 pr-4">مكتبة المعرفة</td>
                  <td className="py-3 pr-4">١,٩٥٠ ر.س</td>
                  <td className="py-3 pr-4"><span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">تم الدفع</span></td>
                </tr>
                <tr>
                  <td className="py-3 pr-4">#١٢٣٠</td>
                  <td className="py-3 pr-4">١٣ مايو ٢٠٢٥</td>
                  <td className="py-3 pr-4">شركة الراية</td>
                  <td className="py-3 pr-4">٥,٦٠٠ ر.س</td>
                  <td className="py-3 pr-4"><span className="bg-red-100 text-red-800 text-xs px-2 py-1 rounded">ملغي</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default Dashboard;
