import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import drilldown from 'highcharts/modules/drilldown';
import { loanApplicationApi } from '../api/loanApplicationApi';
import { getStatusClass } from '../utils/helpers';
import '../styles/DashboardPage.css';

// Initialize drilldown module
drilldown(Highcharts);

function DashboardPage() {
  const [loanApplications, setLoanApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLoanApplications();
  }, []);

  const fetchLoanApplications = async () => {
    try {
      const response = await loanApplicationApi.getAll({ page: 0, size: 100 });
      
      // Handle paginated response
      if (response.data.content) {
        setLoanApplications(response.data.content);
      } else if (Array.isArray(response.data)) {
        setLoanApplications(response.data);
      } else {
        setLoanApplications([]);
      }
    } catch (error) {
      console.error('Error fetching loan applications:', error);
      setLoanApplications([]); // Set empty array on error
    } finally {
      setLoading(false);
    }
  };

  // Pie chart configuration
  const pieChartOptions = {
    chart: {
      plotBackgroundColor: null,
      plotBorderWidth: null,
      plotShadow: false,
      type: 'pie'
    },
    title: {
      text: 'Loan Applications in March, 2020'
    },
    tooltip: {
      pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    accessibility: {
      point: {
        valueSuffix: '%'
      }
    },
    plotOptions: {
      pie: {
        allowPointSelect: true,
        cursor: 'pointer',
        dataLabels: {
          enabled: true,
          format: '<b>{point.name}</b>: {point.percentage:.1f} %'
        }
      }
    },
    series: [{
      name: 'Brands',
      colorByPoint: true,
      data: [
        { name: 'LA', y: 61.41, sliced: true, selected: true },
        { name: 'Sacramento', y: 11.84 },
        { name: 'San Diego', y: 10.85 },
        { name: 'Oakland', y: 4.67 },
        { name: 'Amador', y: 4.18 },
        { name: 'Alamada', y: 1.64 },
        { name: 'Alaska', y: 1.6 },
        { name: 'QQ', y: 1.2 },
        { name: 'Other', y: 2.61 }
      ]
    }]
  };

  // Bar chart configuration
  const barChartOptions = {
    chart: {
      type: 'column'
    },
    title: {
      text: 'Loan Applications. March, 2020'
    },
    accessibility: {
      announceNewData: {
        enabled: true
      }
    },
    xAxis: {
      type: 'category'
    },
    yAxis: {
      title: {
        text: 'Total percent'
      }
    },
    legend: {
      enabled: false
    },
    plotOptions: {
      series: {
        borderWidth: 0,
        dataLabels: {
          enabled: true,
          format: '{point.y:.1f}%'
        }
      }
    },
    tooltip: {
      headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
      pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
    },
    series: [{
      name: "Applications",
      colorByPoint: true,
      data: [
        { name: "LA", y: 62.74, drilldown: "LA" },
        { name: "Sacramento", y: 10.57, drilldown: "Sacramento" },
        { name: "San Diego", y: 7.23, drilldown: "San Diego" },
        { name: "Oakland", y: 5.58, drilldown: "Oakland" },
        { name: "Amador", y: 4.02, drilldown: "Amador" },
        { name: "Other", y: 7.62, drilldown: null }
      ]
    }],
    drilldown: {
      series: [
        {
          name: "LA",
          id: "LA",
          data: [
            ["v65.0", 0.1],
            ["v64.0", 1.3],
            ["v63.0", 53.02],
            ["v62.0", 1.4],
            ["v61.0", 0.88],
            ["v60.0", 0.56],
            ["v59.0", 0.45],
            ["v58.0", 0.49],
            ["v57.0", 0.32],
            ["v56.0", 0.29],
            ["v55.0", 0.79],
            ["v54.0", 0.18],
            ["v51.0", 0.13],
            ["v49.0", 2.16],
            ["v48.0", 0.13],
            ["v47.0", 0.11],
            ["v43.0", 0.17],
            ["v29.0", 0.26]
          ]
        },
        {
          name: "Sacramento",
          id: "Sacramento",
          data: [
            ["v58.0", 1.02],
            ["v57.0", 7.36],
            ["v56.0", 0.35],
            ["v55.0", 0.11],
            ["v54.0", 0.1],
            ["v52.0", 0.95],
            ["v51.0", 0.15],
            ["v50.0", 0.1],
            ["v48.0", 0.31],
            ["v47.0", 0.12]
          ]
        },
        {
          name: "San Diego",
          id: "San Diego",
          data: [
            ["v11.0", 6.2],
            ["v10.0", 0.29],
            ["v9.0", 0.27],
            ["v8.0", 0.47]
          ]
        },
        {
          name: "Oakland",
          id: "Oakland",
          data: [
            ["v11.0", 3.39],
            ["v10.1", 0.96],
            ["v10.0", 0.36],
            ["v9.1", 0.54],
            ["v9.0", 0.13],
            ["v5.1", 0.2]
          ]
        },
        {
          name: "Amador",
          id: "Amador",
          data: [
            ["v16", 2.6],
            ["v15", 0.92],
            ["v14", 0.4],
            ["v13", 0.1]
          ]
        },
        {
          name: "Other",
          id: "Other",
          data: [
            ["v50.0", 0.96],
            ["v49.0", 0.82],
            ["v12.1", 0.14]
          ]
        }
      ]
    }
  };

  const capitalizeStatus = (status) => {
    return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
  };

  if (loading) {
    return <div className="d-flex justify-content-center p-5">Loading...</div>;
  }

  return (
    <div className="dashboard-page">
      <div className="row">
        <div className="col-xs-5">
          <HighchartsReact
            highcharts={Highcharts}
            options={pieChartOptions}
          />
        </div>
        <div className="col-xs-1"></div>
        <div className="col-xs-5">
          <HighchartsReact
            highcharts={Highcharts}
            options={barChartOptions}
          />
        </div>
      </div>

      <hr />

      <div className="page-content">
        <div className="row">
          <div className="col-xs-12">
            <div id="table">
              <h3>Loan applications in April, 2020</h3>
              <table className="table table-bordered table-hover" id="simple-table">
                <thead>
                  <tr>
                    <th>Application ID</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {loanApplications.map((application) => (
                    <tr key={application.id}>
                      <td>{application.id}</td>
                      <td>{application.firstName}</td>
                      <td>{application.lastName}</td>
                      <td>{application.email}</td>
                      <td>{application.phone}</td>
                      <td>
                        <span className={`label label-sm label-${getStatusClass(application.status)} arrowed-in`}>
                          {capitalizeStatus(application.status)}
                        </span>
                      </td>
                      <td>
                        <div className="hidden-sm hidden-xs btn-group">
                          <Link 
                            to={`/loan-applications/${application.id}`}
                            className="btn btn-xs btn-success"
                          >
                            <span className="ace-icon fa fa-check bigger-120">Show</span>
                          </Link>
                        </div>
                        <div className="hidden-sm hidden-xs btn-group">
                          <Link 
                            to={`/loan-applications/${application.id}/edit`}
                            className="btn btn-xs btn-info"
                          >
                            <span className="ace-icon fa fa-pencil bigger-120">Edit</span>
                          </Link>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;