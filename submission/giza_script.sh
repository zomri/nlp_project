 #!/bin/bash

ENG="bible.eng.truncate"
HEB="bible.heb.truncate" 
ENG_HEB="${ENG}_${HEB}.snt"
HEB_ENG="${HEB}_${ENG}.snt"
 
echo 'Hebrew to English'

giza-pp/GIZA++-v2//plain2snt.out $ENG $HEB
giza-pp/GIZA++-v2/snt2cooc.out $ENG $HEB $ENG_HEB > cooc.cooc1
giza-pp/GIZA++-v2/GIZA++ -T $HEB.vcb -S $ENG.vcb -C $ENG_HEB -CoocurrenceFile cooc.cooc1 -o tempPrefix


#rename A3 file as in instructions - delete the rest.
mv tempPrefix.A3.final "${HEB}_${ENG}.A3"
rm tempPrefix.*


giza-pp/GIZA++-v2/snt2cooc.out $HEB $ENG $HEB_ENG > cooc.cooc2
giza-pp/GIZA++-v2/GIZA++ -T $ENG.vcb -S $HEB.vcb -C $HEB_ENG -CoocurrenceFile cooc.cooc2 -o tempPrefix
#rename A3 file as in instructions - delete the rest.

mv tempPrefix.A3.final "${ENG}_${HEB}.A3"
rm tempPrefix.*

rm cooc*
