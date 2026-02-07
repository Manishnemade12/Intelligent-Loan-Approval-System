import DashboardLayout from '@/components/layout/DashboardLayout';
import { useAuth } from '@/contexts/AuthContext';
import StatCard from '@/components/common/StatCard';
import ApplicationsTable from '@/components/applications/ApplicationsTable';
import { mockApplications, mockDashboardStats } from '@/data/mockData';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  FileText,
  TrendingUp,
  Clock,
  CheckCircle,
  XCircle,
  AlertTriangle,
  PlusCircle,
  ArrowRight,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = React.useState<any>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);

  React.useEffect(() => {
    import('../lib/api').then(({ api }) => {
      api.getDashboardStats()
        .then(data => setStats(data))
        .catch(() => setError('Failed to load dashboard stats'))
        .finally(() => setLoading(false));
    });
  }, []);

  const pieData = stats ? [
    { name: 'Approved', value: stats.approved, color: 'hsl(var(--status-approved))' },
    { name: 'Rejected', value: stats.rejected, color: 'hsl(var(--status-rejected))' },
    { name: 'Manual Review', value: stats.manualReview, color: 'hsl(var(--status-review))' },
    { name: 'Pending', value: stats.pending, color: 'hsl(var(--status-pending))' },
  ] : [];

  const recentApplications = mockApplications.slice(0, 5);

  const CustomerDashboard = () => (
    <>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4 mb-6">
        <StatCard
          title="My Applications"
          value={3}
          subtitle="Total submitted"
          icon={FileText}
        />
        <StatCard
          title="Approved"
          value={1}
          icon={CheckCircle}
          variant="success"
        />
        <StatCard
          title="Pending Review"
          value={1}
          icon={Clock}
          variant="warning"
        />
        <StatCard
          title="Total Loan Amount"
          value="$285,000"
          subtitle="Across all applications"
          icon={TrendingUp}
          variant="primary"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>My Applications</CardTitle>
              <CardDescription>Track your loan application status</CardDescription>
            </div>
            <Button onClick={() => navigate('/apply')}>
              <PlusCircle className="w-4 h-4 mr-2" />
              New Application
            </Button>
          </CardHeader>
          <CardContent>
            <ApplicationsTable applications={recentApplications.slice(0, 3)} showActions={false} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <Button variant="outline" className="w-full justify-between" onClick={() => navigate('/apply')}>
              Apply for a new loan
              <ArrowRight className="w-4 h-4" />
            </Button>
            <Button variant="outline" className="w-full justify-between">
              Upload documents
              <ArrowRight className="w-4 h-4" />
            </Button>
            <Button variant="outline" className="w-full justify-between">
              Contact support
              <ArrowRight className="w-4 h-4" />
            </Button>
          </CardContent>
        </Card>
      </div>
    </>
  );

  const OfficerDashboard = () => (
    <>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5 mb-6">
        <StatCard
          title="Total Applications"
          value={stats.totalApplications}
          icon={FileText}
          trend={{ value: 12, isPositive: true }}
        />
        <StatCard
          title="Pending"
          value={stats.pending}
          icon={Clock}
        />
        <StatCard
          title="Approved"
          value={stats.approved}
          icon={CheckCircle}
          variant="success"
        />
        <StatCard
          title="Rejected"
          value={stats.rejected}
          icon={XCircle}
          variant="danger"
        />
        <StatCard
          title="Manual Review"
          value={stats.manualReview}
          icon={AlertTriangle}
          variant="warning"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-3 mb-6">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle>Recent Applications</CardTitle>
            <CardDescription>Latest loan applications requiring attention</CardDescription>
          </CardHeader>
          <CardContent>
            <ApplicationsTable applications={recentApplications} />
          </CardContent>
        </Card>

        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Application Distribution</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-[200px]">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={pieData}
                      cx="50%"
                      cy="50%"
                      innerRadius={50}
                      outerRadius={80}
                      paddingAngle={2}
                      dataKey="value"
                    >
                      {pieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <div className="grid grid-cols-2 gap-2 mt-4">
                {pieData.map((item) => (
                  <div key={item.name} className="flex items-center gap-2 text-sm">
                    <div
                      className="w-3 h-3 rounded-full"
                      style={{ backgroundColor: item.color }}
                    />
                    <span className="text-muted-foreground">{item.name}</span>
                    <span className="font-medium ml-auto">{item.value}</span>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Performance</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-muted-foreground">Approval Rate</span>
                <span className="text-2xl font-bold text-status-approved">{stats.approvalRate}%</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-muted-foreground">Avg Processing Time</span>
                <span className="text-lg font-semibold">{stats.avgProcessingTime}</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </>
  );

  const AdminDashboard = () => (
    <>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5 mb-6">
        <StatCard
          title="Total Applications"
          value={stats.totalApplications}
          icon={FileText}
          trend={{ value: 12, isPositive: true }}
        />
        <StatCard
          title="Approval Rate"
          value={`${stats.approvalRate}%`}
          icon={TrendingUp}
          variant="primary"
        />
        <StatCard
          title="Approved"
          value={stats.approved}
          icon={CheckCircle}
          variant="success"
        />
        <StatCard
          title="Rejected"
          value={stats.rejected}
          icon={XCircle}
          variant="danger"
        />
        <StatCard
          title="Manual Review"
          value={stats.manualReview}
          icon={AlertTriangle}
          variant="warning"
        />
      </div>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>All Applications</CardTitle>
            <CardDescription>Complete overview of loan applications</CardDescription>
          </div>
          <Button variant="outline" onClick={() => navigate('/applications')}>
            View All
            <ArrowRight className="w-4 h-4 ml-2" />
          </Button>
        </CardHeader>
        <CardContent>
          <ApplicationsTable applications={mockApplications} />
        </CardContent>
      </Card>
    </>
  );

  const getDashboardContent = () => {
    switch (user?.role) {
      case 'customer':
        return <CustomerDashboard />;
      case 'officer':
        return <OfficerDashboard />;
      case 'admin':
        return <AdminDashboard />;
      default:
        return <CustomerDashboard />;
    }
  };

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  };

  return (
    <DashboardLayout title="Dashboard">
      <div className="animate-fade-in">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-foreground">
            {getGreeting()}, {user?.name?.split(' ')[0]}
          </h2>
          <p className="text-muted-foreground">
            {user?.role === 'customer'
              ? "Here's an overview of your loan applications"
              : "Here's what's happening with loan applications today"}
          </p>
        </div>

        {getDashboardContent()}
      </div>
    </DashboardLayout>
  );
};

export default Dashboard;
