fetch("/api/v1/admin/option-chart")
    .then(res => res.json())
    .then(optionChart => {
        const labels = optionChart.map(o => o[0]);
        const dataValue = optionChart.map(o => o[1]);

        const data = {
            labels: labels, // 조각 이름
            datasets: [{
                label: '주문 수',
                data: dataValue,
                backgroundColor: [
                    'rgba(79, 195, 247, 0.85)',
                    'rgba(129, 212, 250, 0.85)',
                    'rgba(77, 208, 225, 0.85)',
                    'rgba(144, 202, 249, 0.85)',
                    'rgba(100, 181, 246, 0.85)'
                ],
                borderColor: [
                    'rgba(79, 195, 247, 1)',
                    'rgba(98, 189, 248, 1)',
                    'rgba(77, 208, 225, 1)',
                    'rgba(122, 192, 248, 1)',
                    'rgba(100, 181, 246, 1)'
                ],
                borderWidth: 2,
                hoverOffset: 8,
                hoverBorderWidth: 3
            }]
        };

        const config = {
            type: 'pie',
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                aspectRatio: 1.5,
                layout: {
                    padding: {
                        top: 20,
                        bottom: 20,
                        left: 20,
                        right: 100
                    }
                },
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            padding: 18,
                            boxWidth: 30,
                            boxHeight: 15,
                            boxPadding: 10,
                            font: {
                                size: 13,
                                weight: '500'
                            },
                            color: '#1A237E'
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(255, 255, 255, 0.95)',
                        padding: 12,
                        boxPadding: 10,
                        titleFont: {
                            size: 14,
                            weight: '600'
                        },
                        bodyFont: {
                            size: 13
                        },
                        titleColor: '#1A237E',
                        bodyColor: '#1A237E',
                        borderColor: 'rgba(79, 195, 247, 0.3)',
                        borderWidth: 1,
                        cornerRadius: 8,
                        callbacks: {
                            label: function (context) {
                                return context.label + ': ' + context.parsed + '건';
                            }
                        }
                    }
                }
            }
        };
        new Chart(document.getElementById('optionChart'), config);
    });

fetch("/api/v1/admin/category-revenue-chart")
    .then(res => res.json())
    .then(categoryChart => {
        // categoryChart 예: [['CLOTHING', 500000], ['BEDDING', 300000], ...]
        const labels = categoryChart.map(c => c[0]);
        const revenueValue = categoryChart.map(c => c[1]);

        const data = {
            labels: labels,
            datasets: [{
                label: '매출액',
                data: revenueValue,
                backgroundColor: [
                    '#FF7043', '#FFCA28', '#26A69A', '#42A5F5', '#AB47BC'
                ],
                borderWidth: 1,
                hoverOffset: 10
            }]
        };

        const config = {
            type: 'doughnut', // 도넛형 차트
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'right' },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.label || '';
                                if (label) label += ': ';
                                label += new Intl.NumberFormat('ko-KR').format(context.parsed) + '원';
                                return label;
                            }
                        }
                    }
                }
            }
        };
        new Chart(document.getElementById('categoryChart'), config);
    });

fetch("/api/v1/admin/monthly-trend-chart")
    .then(res => res.json())
    .then(trendData => {
        // trendData 예: [['2023-10', 45, 1200000], ['2023-11', 52, 1500000], ...]
        const labels = trendData.map(t => t[0]); // 년-월
        const counts = trendData.map(t => t[1]); // 주문 건수
        const revenues = trendData.map(t => t[2]); // 매출액

        const data = {
            labels: labels,
            datasets: [
                {
                    type: 'line',
                    label: '주문 건수',
                    data: counts,
                    borderColor: '#FF5252',
                    backgroundColor: '#FF5252',
                    yAxisID: 'y-count', // 오른쪽 축 사용
                    tension: 0.3,
                    fill: false
                },
                {
                    type: 'bar',
                    label: '매출액',
                    data: revenues,
                    backgroundColor: 'rgba(33, 150, 243, 0.6)',
                    yAxisID: 'y-revenue' // 왼쪽 축 사용
                }
            ]
        };

        const config = {
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    'y-revenue': {
                        type: 'linear',
                        position: 'left',
                        title: { display: true, text: '매출액 (원)' },
                        beginAtZero: true
                    },
                    'y-count': {
                        type: 'linear',
                        position: 'right',
                        title: { display: true, text: '주문 건수 (건)' },
                        grid: { drawOnChartArea: false }, // 격자선 중복 방지
                        beginAtZero: true
                    }
                }
            }
        };
        new Chart(document.getElementById('trendChart'), config);
    });