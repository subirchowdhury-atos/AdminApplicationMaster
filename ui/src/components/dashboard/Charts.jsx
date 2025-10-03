
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';

const barChartOptions = {
  series: [{
    name: "Applications",
    colorByPoint: true,
    data: [
      { name: "LA", y: 62.74, drilldown: "LA" },
      { name: "Sacramento", y: 10.57, drilldown: "Sacramento" },
      // ... rest of data
    ]
  }],
  // ... drilldown config
};

function DashboardChart() {
  return <HighchartsReact highcharts={Highcharts} options={barChartOptions} />;
}