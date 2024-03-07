document.addEventListener("DOMContentLoaded", function(){
    setViewer();
    document.getElementById('stop').addEventListener('click', function() {
        if (!eventSource) {
            console.log('SSE already disconnected.');
            return;
        }

        eventSource.close();
        eventSource = null; // 이벤트 소스 객체를 초기화
        console.log('eventSource disconnected.');
        stopScheduler();

    });
    document.getElementById('start').addEventListener('click', function() {
        // 이전에 연결된 eventSource가 있는지 확인하고 있다면 종료
        if (eventSource) {
            eventSource.close();
            console.log('이전 eventSource 연결이 종료되었습니다.');
        }

        startSSE();
    });

    let stopButton = document.getElementById('animation_pathPause');
    stopButton.addEventListener('click', function() {
        stopScheduler();
    });
    let startButton = document.getElementById('animation_pathPlay');
    startButton.addEventListener('click', function() {
        startSSE();
    });

    //timeline click
    viewer.timeline.addEventListener('settime', async function (e) {
        let time = e.timeJulian;
        viewer.clock.currentTime = time;
        let timestamp = convertToTimestampString(time.dayNumber, time.secondsOfDay);
        console.log(timestamp);


        // 클릭한 시간이 현재 시간보다 과거인지
        let currentTime = Cesium.JulianDate.now();
        if (Cesium.JulianDate.lessThan(time, currentTime)) {
            console.log("클릭한 시간이 현재 시간보다 과거입니다.");
            //todo
            document.getElementById('stop').click();

            //클릭한 시간데이터 /api/sse-past
            getData.past(time.dayNumber, time.secondsOfDay,timestamp );

        } else if (Cesium.JulianDate.equals(time, currentTime)) {
            console.log("클릭한 시간이 현재입니다.");
        }

    });

    startSSE();
});

function convertToTimestampString(dayNumber, secondsOfDay) {
    // UTC 기준 시간을 구합니다.
    let utcDate = new Date((dayNumber - 2440588) * 24 * 60 * 60 * 1000);
    let hour = Math.floor(secondsOfDay / 3600);
    let minute = Math.floor((secondsOfDay % 3600) / 60);
    let second = Math.floor(secondsOfDay % 60);

    utcDate.setUTCHours(hour + 21);
    utcDate.setUTCMinutes(minute);
    utcDate.setUTCSeconds(second);
    utcDate.setUTCMilliseconds(0);

    let koreanTimeString = utcDate.toISOString().slice(0, 19).replace('T', ' ');
    return koreanTimeString;
}



let viewer;
const DateTime = luxon.DateTime;
function setViewer(){
    viewer = new Cesium.Viewer('cesiumContainer');
    let destination = new Cesium.Cartesian3(
         -3052094.097987919,
        4053864.1338042715,
        3853706.6392154032
    )
    // let destination = Cesium.Cartesian3.fromDegrees(126.9739802, 37.39965698, 7000);
    // 특정 위치로 이동
    viewer.scene.camera.setView({
        destination: destination,
        shouldAnimate: true,
    });

    viewer.animation.viewModel.timeFormatter = function(date, viewModel) {
        const isoString = Cesium.JulianDate.toIso8601(date);
        let dateTime = DateTime.fromISO(isoString);
        dateTime = dateTime.setZone("Asia/Seoul");

        return dateTime.toFormat('HH:mm:ss');
    };

    const now = Cesium.JulianDate.now();
    const oneHourBefore = Cesium.JulianDate.addHours(now, -1, new Cesium.JulianDate());
    const oneHourAfter = Cesium.JulianDate.addHours(now, 1, new Cesium.JulianDate());
    const timeline = viewer.timeline;
    timeline.zoomTo(oneHourBefore, oneHourAfter);

}

let eventSource;
function draw(data) {
    //viewer.entities.removeAll();

    data.forEach(item => {
        // 객체 위치
        const position = Cesium.Cartesian3.fromDegrees(item.lon, item.lat);
        // SampledPositionProperty 생성
        const property = new Cesium.SampledPositionProperty();
        // 초기 위치 설정
        const isoString2 = item.time;
        const dateTime2 = new Date(isoString2); // 문자열로부터 Date 객체 생성
        const julianDate2 = Cesium.JulianDate.fromDate(dateTime2);
        property.addSample(julianDate2, position);

        let color = [
            Cesium.Color.ALICEBLUE,
            Cesium.Color.RED,
            Cesium.Color.ORANGE,
            Cesium.Color.YELLOW,
            Cesium.Color.GREEN,
            Cesium.Color.CYAN,
            Cesium.Color.BLUE,
            Cesium.Color.NAVY,
            Cesium.Color.PURPLE,
            Cesium.Color.PINK
        ];

        // 객체 생성
        let entity = {
            name: `${item.kindName} ${item.kindNum}`,
            position: property
        };

        if (item.kindSeq === 1) {
            entity.box = {
                dimensions: new Cesium.Cartesian3(7.0, 7.0, 7.0),
                material: color[item.kindNum]
            };
        } else if (item.kindSeq === 2) {
            entity.ellipsoid = {
                radii: new Cesium.Cartesian3(5.0, 5.0, 5.0),
                material: color[item.kindNum]
            };
        }

        viewer.entities.add(entity);
        // billboard : {
        //     image : item.kindSeq === 1 ? '/static/img/car.png': '/static/img/motorcycle.png',
        //     scale : 0.07,
        //     color: color[item.kindNum - 1],
        // }

        // 현재 시간을 한국 시간대로 설정
        const isoString = item.time; // 예: '2024-02-21 17:35:55.000 +0900'
        const dateTime = DateTime.fromISO(isoString).setZone('Asia/Seoul');
        const julianDate = Cesium.JulianDate.fromDate(dateTime.toJSDate());
        viewer.clock.currentTime = julianDate;

        // 시계 이벤트 리스너를 사용하여 시간이 흐를 때마다 객체의 위치를 업데이트
        viewer.clock.onTick.addEventListener(function(clock) {
            entity.position = property.getValue(clock.currentTime);
        });

    });
}

function startSSE() {
    // SSE 연결
    eventSource = new EventSource('/api/sse-real/');

    // SSE 수신
    eventSource.onmessage = function(event) {
        let jsonData = JSON.parse(event.data);
        console.log(jsonData);
        draw(jsonData);
    };

    // SSE 연결 종료
    eventSource.onerror = function(event) {
        console.error('eventSource failed:', event);
    };

    console.log('eventSource SSE connected.');
}


function stopScheduler() {
    fetch('/api/stop/', {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to stop scheduler');
            }
            console.log('Scheduler stopped');
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

const getData = {
    coords: async function(dayNumber, secondsOfDay) {
        try {
            const response = await fetch('/api/data/?dayNumber='+dayNumber+'&secondsOfDay='+secondsOfDay);
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    },
    past: function(dayNumber, secondsOfDay, time) {
        // 이전에 연결된 eventSource가 있는지 확인하고 있다면 종료
        if (eventSource) {
            eventSource.close();
            console.log('이전 eventSource 연결이 종료되었습니다.');
        }

        // 새로운 SSE 연결 설정
        // eventSource = new EventSource('/api/sse-past/?dayNumber='+dayNumber+'&secondsOfDay='+secondsOfDay);
        eventSource = new EventSource('/api/sse-past/?time='+time);

        // SSE 수신
        eventSource.onmessage = function(event) {
            let jsonData = JSON.parse(event.data);
            // console.log(jsonData);
            draw(jsonData);
        };

        // SSE 연결 오류 처리
        eventSource.onerror = function(event) {
            console.error('eventSource 연결 실패:', event);
        };

        console.log('eventSource SSE 연결 완료.');
    },
};


