
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
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

interface User {
  id: number;
  name: string;
  email: string;
  role: "admin" | "sales" | "accountant" | "warehouse";
  lastActive: string;
  status: "active" | "inactive";
}

const Users = () => {
  const [searchTerm, setSearchTerm] = useState("");
  
  // Mock user data
  const users: User[] = [
    { id: 1, name: "أحمد محمد", email: "ahmed@tanzeem.com", role: "admin", lastActive: "منذ ١٠ دقائق", status: "active" },
    { id: 2, name: "فاطمة علي", email: "fatima@tanzeem.com", role: "sales", lastActive: "منذ ٢٠ دقيقة", status: "active" },
    { id: 3, name: "خالد عبدالله", email: "khalid@tanzeem.com", role: "accountant", lastActive: "منذ ١ ساعة", status: "active" },
    { id: 4, name: "نورة سعد", email: "noura@tanzeem.com", role: "warehouse", lastActive: "منذ ٢ ساعة", status: "active" },
    { id: 5, name: "محمد عبدالرحمن", email: "mohammed@tanzeem.com", role: "sales", lastActive: "منذ يوم", status: "inactive" },
  ];
  
  // Filter users based on search term
  const filteredUsers = users.filter(
    (user) =>
      user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.role.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getRoleBadge = (role: string) => {
    switch (role) {
      case "admin":
        return <Badge className="bg-purple-100 text-purple-800 hover:bg-purple-100">مدير</Badge>;
      case "sales":
        return <Badge className="bg-blue-100 text-blue-800 hover:bg-blue-100">مبيعات</Badge>;
      case "accountant":
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">محاسب</Badge>;
      case "warehouse":
        return <Badge className="bg-amber-100 text-amber-800 hover:bg-amber-100">مسؤول مخزن</Badge>;
      default:
        return null;
    }
  };

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase();
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
          إضافة مستخدم
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>قائمة المستخدمين</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-right">
              <thead>
                <tr className="border-b">
                  <th className="pb-2 pr-4 text-sm font-medium">المستخدم</th>
                  <th className="pb-2 pr-4 text-sm font-medium">البريد الإلكتروني</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الدور</th>
                  <th className="pb-2 pr-4 text-sm font-medium">آخر نشاط</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الحالة</th>
                  <th className="pb-2 pr-4 text-sm font-medium">الإجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((user) => (
                  <tr key={user.id} className="border-b">
                    <td className="py-3 pr-4">
                      <div className="flex items-center gap-3">
                        <Avatar>
                          <AvatarFallback>{getInitials(user.name)}</AvatarFallback>
                        </Avatar>
                        <span className="font-medium">{user.name}</span>
                      </div>
                    </td>
                    <td className="py-3 pr-4">{user.email}</td>
                    <td className="py-3 pr-4">
                      {getRoleBadge(user.role)}
                    </td>
                    <td className="py-3 pr-4">{user.lastActive}</td>
                    <td className="py-3 pr-4">
                      <div className="flex items-center">
                        <span className={`h-2 w-2 rounded-full mr-2 ${user.status === 'active' ? 'bg-green-500' : 'bg-gray-400'}`}></span>
                        {user.status === 'active' ? 'نشط' : 'غير نشط'}
                      </div>
                    </td>
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
            إجمالي المستخدمين: {filteredUsers.length}
          </div>
        </CardFooter>
      </Card>
    </div>
  );
};

export default Users;
