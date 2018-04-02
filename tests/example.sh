if ((java -cp bin vknyazev_ConnectN.ConnectNGameInterface < tests/example.test) 2>&1 | cmp -s tests/example.result)
then
echo "Match"
else
echo "No match"
fi