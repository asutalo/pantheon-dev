while read -r m; do
#  ako nije module, pokreni sve testove?
  grep -q "$m" ../settings.gradle; [ $? -eq 0 ] && echo "identified module $m, running tests" || echo "$m is not a module, skipping tests"
done <<<  "$(sort -u <<< "$(git status --porcelain | awk '{print $2}' | awk -F/ '{print $1}')")"
