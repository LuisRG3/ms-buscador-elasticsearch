# ms-buscador-elasticsearch
# PAra cargar la data usar el siguiente codigo en la carpeta donde esta el archivo "output.json"

curl -XPUT "https://8kvaqsd66n:9t1velbtnx@unir-cluster-2968630583.us-east-1.bonsaisearch.net:443/_bulk" --data-binary @output.json -H "Content-Type: application/json"