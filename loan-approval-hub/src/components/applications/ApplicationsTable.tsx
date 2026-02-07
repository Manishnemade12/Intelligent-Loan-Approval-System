import { useState } from 'react';
import { LoanApplication } from '@/types/loan';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import StatusBadge from '@/components/common/StatusBadge';
import { Search, Eye, ArrowUpDown, FileText } from 'lucide-react';
import { format } from 'date-fns';
import { cn } from '@/lib/utils';
import { useNavigate } from 'react-router-dom';

interface ApplicationsTableProps {
  applications: LoanApplication[];
  showActions?: boolean;
}

const ApplicationsTable = ({ applications, showActions = true }: ApplicationsTableProps) => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [sortField, setSortField] = useState<'submittedAt' | 'loanAmount' | 'riskScore'>('submittedAt');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  const filteredApplications = applications
    .filter((app) => {
      const matchesSearch =
        app.applicantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        app.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
        app.email.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesStatus = statusFilter === 'all' || app.status === statusFilter;
      return matchesSearch && matchesStatus;
    })
    .sort((a, b) => {
      const aVal = a[sortField];
      const bVal = b[sortField];
      const modifier = sortDirection === 'asc' ? 1 : -1;
      return aVal > bVal ? modifier : -modifier;
    });

  const handleSort = (field: typeof sortField) => {
    if (sortField === field) {
      setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortDirection('desc');
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
    }).format(amount);
  };

  const getLoanTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      personal: 'Personal',
      home: 'Home',
      auto: 'Auto',
      business: 'Business',
      education: 'Education',
    };
    return labels[type] || type;
  };

  return (
    <div className="space-y-4">
      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search by name, ID, or email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Select value={statusFilter} onValueChange={setStatusFilter}>
          <SelectTrigger className="w-full sm:w-40">
            <SelectValue placeholder="Status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Status</SelectItem>
            <SelectItem value="pending">Pending</SelectItem>
            <SelectItem value="approved">Approved</SelectItem>
            <SelectItem value="rejected">Rejected</SelectItem>
            <SelectItem value="manual_review">Manual Review</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Table */}
      <div className="border rounded-lg overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-secondary/50">
              <TableHead className="font-semibold">Application ID</TableHead>
              <TableHead className="font-semibold">Applicant</TableHead>
              <TableHead className="font-semibold">Type</TableHead>
              <TableHead className="font-semibold">
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-auto p-0 hover:bg-transparent"
                  onClick={() => handleSort('loanAmount')}
                >
                  Amount
                  <ArrowUpDown className="ml-1 h-3 w-3" />
                </Button>
              </TableHead>
              <TableHead className="font-semibold">
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-auto p-0 hover:bg-transparent"
                  onClick={() => handleSort('riskScore')}
                >
                  Risk Score
                  <ArrowUpDown className="ml-1 h-3 w-3" />
                </Button>
              </TableHead>
              <TableHead className="font-semibold">Status</TableHead>
              <TableHead className="font-semibold">
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-auto p-0 hover:bg-transparent"
                  onClick={() => handleSort('submittedAt')}
                >
                  Submitted
                  <ArrowUpDown className="ml-1 h-3 w-3" />
                </Button>
              </TableHead>
              {showActions && <TableHead className="font-semibold text-right">Actions</TableHead>}
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredApplications.length === 0 ? (
              <TableRow>
                <TableCell colSpan={showActions ? 8 : 7} className="text-center py-8">
                  <FileText className="w-12 h-12 mx-auto text-muted-foreground/50 mb-2" />
                  <p className="text-muted-foreground">No applications found</p>
                </TableCell>
              </TableRow>
            ) : (
              filteredApplications.map((app) => (
                <TableRow
                  key={app.id}
                  className="cursor-pointer hover:bg-secondary/50 transition-colors"
                  onClick={() => navigate(`/application/${app.id}`)}
                >
                  <TableCell className="font-mono text-sm">{app.id}</TableCell>
                  <TableCell>
                    <div>
                      <p className="font-medium">{app.applicantName}</p>
                      <p className="text-sm text-muted-foreground">{app.email}</p>
                    </div>
                  </TableCell>
                  <TableCell>
                    <span className="px-2 py-1 bg-secondary text-secondary-foreground rounded text-sm">
                      {getLoanTypeLabel(app.loanType)}
                    </span>
                  </TableCell>
                  <TableCell className="font-medium">{formatCurrency(app.loanAmount)}</TableCell>
                  <TableCell>
                    <span
                      className={cn(
                        'font-medium',
                        app.riskScore <= 30
                          ? 'text-status-approved'
                          : app.riskScore <= 60
                          ? 'text-status-review'
                          : 'text-status-rejected'
                      )}
                    >
                      {app.riskScore}
                    </span>
                  </TableCell>
                  <TableCell>
                    <StatusBadge status={app.status} size="sm" />
                  </TableCell>
                  <TableCell className="text-muted-foreground">
                    {format(new Date(app.submittedAt), 'MMM d, yyyy')}
                  </TableCell>
                  {showActions && (
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/application/${app.id}`);
                        }}
                      >
                        <Eye className="w-4 h-4 mr-1" />
                        View
                      </Button>
                    </TableCell>
                  )}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {/* Results count */}
      <p className="text-sm text-muted-foreground">
        Showing {filteredApplications.length} of {applications.length} applications
      </p>
    </div>
  );
};

export default ApplicationsTable;
