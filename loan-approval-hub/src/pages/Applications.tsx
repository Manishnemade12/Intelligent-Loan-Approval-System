import DashboardLayout from '@/components/layout/DashboardLayout';
import ApplicationsTable from '@/components/applications/ApplicationsTable';
import { mockApplications } from '@/data/mockData';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

const ApplicationsPage = () => {
  return (
    <DashboardLayout title="Applications">
      <div className="animate-fade-in">
        <Card>
          <CardHeader>
            <CardTitle>Loan Applications</CardTitle>
            <CardDescription>View and manage all loan applications</CardDescription>
          </CardHeader>
          <CardContent>
            <ApplicationsTable applications={mockApplications} />
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
};

export default ApplicationsPage;
