/*
* Copyright 2010-2020 EMBL - European Bioinformatics Institute
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
* file except in compliance with the License. You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software distributed under the
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
* CONDITIONS OF ANY KIND, either express or implied. See the License for the
* specific language governing permissions and limitations under the License.
*/
package uk.ac.ebi.ena.readtools.loader.bam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;

import uk.ac.ebi.ena.readtools.loader.common.eater.DataConsumerException;
import uk.ac.ebi.ena.readtools.loader.common.eater.PrintDataEater;
import uk.ac.ebi.ena.readtools.loader.common.feeder.DataProducerException;
import uk.ac.ebi.ena.readtools.loader.fastq.IlluminaSpot;

public class 
LoaderTest
{
    @Before
    public void
    init()
    {
       
    }
    
    @After
    public void
    unwind()
    {
        
    }
    

    boolean 
    read( InputStream is, String name ) throws SecurityException, DataProducerException, NoSuchMethodException, IOException, InterruptedException
    {
        BamEater eater = new BamEater( new File( "." ), 
                                       false, //read_type == IlluminaVDB2Eater.READ_TYPE.PAIRED, 
                                       true, 
                                       2 );
        eater.setVerbose( true );
        eater.setConsumer( new PrintDataEater<IlluminaSpot, Object>() );
        BamFeeder feeder = new BamFeeder( is ); 
        feeder.setName( name );
        feeder.setConsumer( eater );
        feeder.start();
        
        feeder.join();
        return feeder.isOk();
    }
    
    
    boolean 
    read( InputStream is1, 
          InputStream is2, 
          String name ) throws SecurityException, DataProducerException, NoSuchMethodException, IOException, InterruptedException, DataConsumerException
    {
        BamEater eater = new BamEater( new File( "." ), 
                                       true, 
                                       true, 
                                       2 );
        //eater.setVerbose( true );
        eater.setConsumer( new PrintDataEater<IlluminaSpot, Object>() );
        
        BamFeeder feeder1 = new BamFeeder( is1 ); 
        feeder1.setName( name + ".1" );
        feeder1.setConsumer( eater );
        feeder1.start();
        /*
        BamFeeder feeder2 = new BamFeeder( is2 ); 
        feeder2.setName( name + ".2" );
        feeder2.setEater( eater );
        feeder2.start();
         */
        feeder1.join();
        //feeder2.join();
        
        eater.cascadeErrors();
        return eater.isOk() && feeder1.isOk();// && feeder2.isOk();
    }

    
    boolean 
    read( File resource1, 
          File resource2,
          String name ) throws Exception
    {
        InputStream is1 = new BufferedInputStream( new FileInputStream( resource1 ) );
        InputStream is2 = new BufferedInputStream( new FileInputStream( resource2 ) );
        try
        {
            return read( is1, is2, name );
        } finally
        {
            is1.close();
            is2.close();
        }
    }
    
    
    boolean 
    read( String resource ) throws Exception
    {
        InputStream is = getClass().getResourceAsStream( resource );
        try
        {
            return read( is, resource );
        } finally
        {
            is.close();
        }
    }
    
    
    boolean 
    read( File file ) throws Exception
    {
        InputStream is = new BufferedInputStream( new FileInputStream( file ) );
        try
        {
            return read( is, file.getPath() );
        } finally
        {
            is.close();
        }
    }

    
    
    @org.junit.Test
    public void
    testCorrect() throws Exception
    {
        if( !read( new File( "resources/bnlx1mp_srt.header.bam" ),
                   new File( "resources/bnlx1mp_srt.header.bam" ), 
                   "bnlx1mp_srt.header.bam" ) )
            throw new Exception( "fail!" );
       
    }
    
    
    @org.junit.Test
    public void
    testFailed() throws Exception
    {
//        if( read( "fastq_casava1_8_incorrect.txt", QualityNormalizer.SANGER ) )
//            throw new Exception( "fail!" );

    }
    
    
    
}
