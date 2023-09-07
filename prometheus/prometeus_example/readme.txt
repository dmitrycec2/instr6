1) Для того чтобы данные экспортировались из jmeter в prometheus, необходимо между машинами открыть порты 9090 и 9270

2) Настройка прометея:
Перейдите в папку установки Prometheus и откройте prometheus.yml.
Добавьте приведенную ниже информацию в ваш yml-файл (пример находится в файле prometeus_example\config files\prometheus.yml), вместо localhosh укажите адрес машины с jmeter
  - job_name: 'jmeter'
    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.
    static_configs:
    - targets: ['localhost:9270']

перезапустите prometheus --config.file=prometheus.yml

3) Откройте графану, создайте datasource прометея,  импортируйте панель prometeus_example\config files\prometeus_jmeter_test.json

4) Откройте jmeter, откройте simple_prometheus_example.jmx, запустите тест, проверьте что поступают данные в графане

jmeter.properties указать:
prometheus.ip=192.168.1.166
prometheus.port=9270
чтобы было доступно не только с localhost