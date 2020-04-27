file = open('./_frame_yijia')
# file = open('../../_fps/bmm_with_only_beaty_realtime_fps')
# file = open('../../_fps/bmm_with_beauty_without_render_api_cost')
# file = open('../../_fps/bmm_with_beauty_api_cost')
# file = open('../../_fps/bmm_api_cost')
sum_time = 0
times = 0

for line in file:
    point = line.split('fps: ')
    sum_time += int(point[1])
    times += 1

print('{} {:.2f}'.format(times, sum_time/times))