DROP PROCEDURE IF EXISTS generate_test_listings;
CREATE PROCEDURE generate_test_listings(IN num_rows INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE new_id CHAR(36);

    WHILE i < num_rows
        DO
            SET new_id = UUID();

            INSERT INTO listings (id, author, created_at, updated_at, category, title, description,
                                  price, lat, lon)
            VALUES (new_id,
                    '95829483',
                    NOW(),
                    NOW(),
                    'Sykler',
                    CONCAT('Sykkel ', i),
                    CONCAT('Selger min kjære følgesvenn gjennom de siste tre årene – en nydelig restaurert Nakamura Crossover 3.0 fra 2018. Denne allsidige sykkelen har vært min trofaste turkamerat på både grusveier og asfalt, men nå er det på tide at den finner en ny eier som kan ta den med på nye eventyr.

Tekniske spesifikasjoner:
Ramme: Aluminium, størrelse 54 cm (passer for høyde 170-185 cm)
Gir: Shimano Deore 3x10 (nylig justert og fungerer knirkefritt)
Bremser: Hydrauliske skivebremser fra Shimano (nye bremseklosser montert høsten 2024)
Hjul: 28" med Continental CrossRide dekk (lite slitasje, mye liv igjen)
Vekt: Ca. 11,5 kg
Tilstand:
Sykkelen fremstår i eksepsjonelt god stand. Rammen har ingen bulker eller skader, kun minimale bruksmerker som er uunngåelig. Drivverket er nylig rengjort og smurt, og fungerer som det skal. Kjedet ble byttet for ca. 500 km siden og har fortsatt god levetid foran seg.

Oppgraderinger:
Ergonomisk Brooks B17 sadel (verdi kr 1.200,-)
SKS skjermsett for helårsbruk (avtagbart)
Topeak flaskeholder i aluminium
Kattøye frontlykt og baklykt (USB-oppladbare)
Ny styretape i premium kork
Brukshistorikk:
Sykkelen har primært vært brukt til pendling og helgeturer på asfalt og lettere grusveier. Alltid oppbevart innendørs og vedlikeholdt etter behov. Total kjørelengde er ca. 3.500 km, noe som er beskjedent for en kvalitetssykkel som denne.

Hvorfor selger jeg?
Jeg har nylig investert i en spesialisert terrengsykkel og trenger dessverre ikke to sykler i garasjen. Denne fortjener en aktiv eier som vil sette pris på dens allsidige egenskaper.

Sykkelen selges komplett som beskrevet, inkludert alle oppgraderinger. Originale manualer og kvittering følger med. Kan prøvekjøres etter avtale i Oslo-området.

Ta kontakt for flere bilder eller spørsmål. Sykkelen er klar for umiddelbar overtakelse!', i),
                    FLOOR(RAND() * 100000),
                    40.730610 + (RAND() * 0.1),
                    -73.935242 + (RAND() * 0.1));

            -- Insert into listing_images table
            INSERT INTO listing_images (listing_id, image_id)
            VALUES (new_id, 'ec2a8f6f-b5cd-48db-90a6-1569d9496206');

            SET i = i + 1;
        END WHILE;
END;

CALL generate_test_listings(1000);
