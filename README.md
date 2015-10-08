# drill-gis

drill-gis is a plugin for Apache Drill that supports spatial queries in a way similar to Postgres PostGIS extension. I came with the idea during [Tugdual Grall's talk] on Apache Drill at [Apache Big Data]. The first prototype was ready the next day after the talk.
Currently it is at proposal/proof-of-concept stage.

What is supported:
  - geometries with initial native binary representation (WKB)
  - functions to create geometries *ST_Point(lon, lat)*, *ST_GeomFromText(wkt_text)*
  - spatial functions *ST_DWithin(geom, geom, distance)*, *ST_Within(geom, geom)*
  - function to convert geometry from binary to text *ST_AsText*

For an overview of spatial functions based on PostGIS you may refer: [PostGIS documentation]

## Installation
Clone/download the sourcecode and run:
```sh
$ mvn clean package
```
then copy target/drill-gis* jars to drill's jars/3rdparty directory. Also ensure that to copy esri-geometry-api-1.2.1.jar in the same place.

You can also build [my fork of Apache Drill with drill-gis] implemented as a plugin. Then just run
```sh
$ mvn clean install
```

After building the code you can start Drill with:
```sh
$ bin/drill-embedded
```

## Usage

### Sample dataset

There is a sample CSV dataset included, which is a subset of [cities of the world] dataset for US California state ***CA-cities.csv*** (~5k rows). You can copy it to Drill's sample-data directory.

The structure of the CSV is as follows:
```
country, state_code, city, latitude, longitude
```

Following examples are based on local queries i.e.:
```
select * from dfs.default.`/home/k255/drill/sample-data/CA-cities.csv` limit 5;
```

when using [my fork of Apache Drill with drill-gis], sample file is on classpath:

```
select * from cp.`sample-data/CA-cities.csv` limit 5;
```

You can see the dataset visualized here:
* http://bl.ocks.org/anonymous/raw/20d87dd21e936ea3d314/

### Spatial queries

Creating a simple (binary) geometry is as simple as:
```
select *, ST_GeomFromText('POINT(-121.895 37.340)') as geom
    from dfs.`default`.`/home/k255/drill/sample-data/CA-cities.csv` limit 1;
```
Creating point geometry based on data field:
```
select *, ST_Point(columns[4], columns[3]) as geom
    from dfs.`default`.`/home/k255/drill/sample-data/CA-cities.csv` limit 1;
```

Common use case is to limit to boundary of given polygon:

```
select columns[2] as city, columns[4] as lon, columns[3] as lat
    from dfs.`default`.`/home/k255/drill/sample-data/CA-cities.csv`
    where
        ST_Within(
            ST_Point(columns[4], columns[3]),
            ST_GeomFromText(
                'POLYGON((-121.95 37.28, -121.94 37.35, -121.84 37.35, -121.84 37.28, -121.95 37.28))'
                )
            );
```

which limits rows of our dataset to region near San Jose as shown here:
* bl.ocks.org/d/ad56a1c850d03675c2d9/

Rows can be limited also based on distance (decimal degrees in this case) from given geometry:

```
select * from
    (select columns[2] as location, columns[4] as lon, columns[3] as lat,
        ST_DWithin(ST_Point(-121.895, 37.339), ST_Point(columns[4], columns[3]), 0.1) as isWithin
        from dfs.`default`.`/home/k255/drill/sample-data/CA-cities.csv`
    )
    where isWithin = true;
```
In this example rows are filtered based on distance (0.1deg ~= 11km) from San Jose city:
* bl.ocks.org/d/cc5a6d695f3a915db5ad/

### Issues

 - varbinary in Apache Drill can hold up to 255 bytes which limits more complex geometries

## Author

Karol Potocki

## License
----

Apache 2.0 License



   [Apache Big Data]: <http://events.linuxfoundation.org/events/apache-big-data-europe>
   [my fork of Apache Drill with drill-gis]: <https://github.com/k255/drill.git>
   [Tugdual Grall's talk]: <http://events.linuxfoundation.org/sites/events/files/slides/apache_drill_budapest_2015.pdf>
   [cities of the world]: <http://www.opengeocode.org/download.php#cities>
   [PostGIS documentation]: <http://postgis.net/docs/reference.html>
