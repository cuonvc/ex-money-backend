#!/bin/bash

date=$(date '+%Y%m%d%H%M%S');
pg_dump -U postgres -h localhost -d exmoney > ~/workspace/backup/exmoney/database/${date}.sql;