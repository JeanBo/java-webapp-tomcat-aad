package za.co.liberty.web.pages.testing;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import za.co.liberty.agreement.common.AgreementObjectReference;
import za.co.liberty.agreement.domain.actual.RequestDOLocalHome;
import za.co.liberty.agreement.domain.spec.util.agreement.CurrencyAmountUtil;
import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.business.request.IRequestManagement;
import za.co.liberty.business.userprofiles.AgreementPrivilegeManagement;
import za.co.liberty.business.userprofiles.IAgreementPrivilegeManagement;
import za.co.liberty.business.webservice.geocode.IGeocodingServiceManagement;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.party.aqcdetail.AQCDetailsWithTypeDTO;
import za.co.liberty.dto.party.aqcdetail.AdvisorQualityCodeDTO;
import za.co.liberty.dto.party.aqcdetail.EffectiveAQCDTO;
import za.co.liberty.dto.party.contactdetail.PhysicalAddressDTO;
import za.co.liberty.dto.rating.ExternalReferenceDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.helpers.communication.MailHelper;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.party.IExplicitAgreementEntityManager;
import za.co.liberty.persistence.party.IPartyProfileEntityManager;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileEntity;
import za.co.liberty.persistence.party.entity.userprofile.PartyProfileExplicitAgreementsEntity;
import za.co.liberty.persistence.rating.ExternalReferenceEntityManager;
import za.co.liberty.persistence.rating.IExternalReferenceEntityManager;
import za.co.liberty.persistence.srs.IWebServiceMessagesEntityManager;
import za.co.liberty.rules.exceptions.RuleLimitArgumentRuntimeException;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.AjaxDownload;

/**
 * A test page used for displaying data stored in the GuiRequest table. 
 * 
 * @author JZB0608 - 09 Apr 2009
 *
 */
public class JeanTestPage extends BasePage {
	
	private static final long serialVersionUID = 1L;

	public JeanTestPage() {
		this(null);
	}
	public JeanTestPage(PageParameters parms) {
		
		logger = Logger.getLogger(this.getClass());
		
		
		System.out.println("Parms = " + (parms == null? null : parms));
		if (parms == null) {
			AgreementObjectReference[] resp = null;
			try {
				AgreementObjectReference obj = new AgreementObjectReference();
				obj.setObjectOid(1039521920L);

				InitialContext ictx = new InitialContext();
				RequestDOLocalHome home = (RequestDOLocalHome) ictx.lookup("ejblocal:ejb/za/co/liberty/agreement/domain/actual/RequestDO");
				
				System.out.println("Home : " + home);
				
				resp = home.getTopLevelOnlyActualReferences(
						new ApplicationContext(),new AgreementObjectReference[] {obj});
				
//				AgreementObjectReference[] resp = AgreementHomeFactory.getInstance().getRequestDOLocalHome()
				System.out.println("Output:" + resp);
					
//					.getReferencesForKindsAndTargetActualKindsAndPropertiesAndDatesAndTopLevelOnlyActuals(
//							appContext, requestKindEnums, targetActualKinds, propertyKindEnums, propertyValues, requestedDateFrom, requestedDateTo, requestDateFrom, requestDateTo, executedDateFrom, executedDateTo, onlyActualReferences, restriction)
				
				
//				IScheduledCommunicationEntityManager em = ServiceLocator.lookupService(IScheduledCommunicationEntityManager.class);
//				Collection<ScheduledCommunicationEntity> list = em.findAllErrors();
//				blankTest("Got findAllScheduledCommunicationEntities response with count=" + list.size());
////				Collection<ScheduledCommunicationFLO> list = em.findAllScheduledCommunicationFLOs(new Date());
////				blankTest("Got findAllScheduledCommunicationFLOs response with count=" + list.size());
//				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			blankTest("Result:" + ((resp==null)?"null response" : ""+resp[0].getObjectOid()));
			
		} else if (!parms.get("logXML").isNull()) {
			String msg = null;
			try {
				IWebServiceMessagesEntityManager controller = ServiceLocator.lookupService(
						IWebServiceMessagesEntityManager.class);
				controller.persistBroadcastMessage(null);
				logger.info("After save");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg = "ERROR - " + e.getExplanation();
			}
			blankTest("Insert logXML :" + msg);
		} else if (!parms.get("autogen").isNull()) {
			 // call with 2311 for mid six   &autogen=2311		
			//		http://localhost:9080/SRSAppWeb/?wicket:bookmarkablePage=:za.co.liberty.web.pages.testing.JeanTestPage&autogen=2311
			// call with 2361 for last four, include anchor (mid six)  --   http://localhost:9080/SRSAppWeb/?wicket:bookmarkablePage=:za.co.liberty.web.pages.testing.JeanTestPage&autogen=2361&anchor=000002
			 Long type = parms.get("autogen").toLongObject();
			 Long roleplayerTypeId = null;
			 String anchor = null;
			 
			 if (!parms.get("anchor").isNull()) {
				 anchor = parms.get("anchor").toString();
			 }
			 if (anchor != null && type==2361L) {
				 // Last four
				 roleplayerTypeId = 2311L;
			 }
			 boolean callRelease = false;
			 boolean callCommit = false;
			 boolean recover = false;
			 
			 String reference = null;
			 if (!parms.get("callRelease").isNull()) {
				 callRelease = true;
				 reference = parms.get("callRelease").toString();
			 } else if (!parms.get("commit").isNull()) {
				 callCommit = true;
				 reference = parms.get("commit").toString();
			 } else if (!parms.get("recover").isNull()) {
				 recover = true;
				 reference = parms.get("recover").toString();
			 }
		
			 logger.info("before type="+type
						+",role=" + roleplayerTypeId
						+",anchor=" + anchor);
			 
			 Logger.getLogger(ExternalReferenceEntityManager.class).setLevel(Level.DEBUG);
			 
			/*
			 * Cons code generation test
			 */
			try {
				IExternalReferenceEntityManager controller = ServiceLocator.lookupService(
						IExternalReferenceEntityManager.class);
				ExternalReferenceDTO refDto = null;
				
				
				if (recover) {
					refDto = new ExternalReferenceDTO();
					refDto.setAnchor(anchor);
					refDto.setNodeType(type);
					refDto.setRoleplayerTypeId(roleplayerTypeId);
					refDto.setReference(reference);
					
					boolean result = controller.recoverReference(refDto);
					blankTest("type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - result = " + result);
					
					logger.info("after (call passed) - type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - result = " + result);
				} else if (callCommit) {
					refDto = new ExternalReferenceDTO();
					refDto.setAnchor(anchor);
					refDto.setNodeType(type);
					refDto.setRoleplayerTypeId(roleplayerTypeId);
					refDto.setReference(reference);
					
					boolean result = controller.commitReservedReference(refDto);
					blankTest("type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - result = " + result);
					
					logger.info("after (call passed) - type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - result = " + result);
				} else if (callRelease) {
					refDto = new ExternalReferenceDTO();
					refDto.setAnchor(anchor);
					refDto.setNodeType(type);
					refDto.setRoleplayerTypeId(roleplayerTypeId);
					refDto.setReference(reference);
					
					boolean result = controller.releaseReservedReference(refDto);
					blankTest("type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - result = " + result);
					
					logger.info("after (call passed) - type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - result = " + result);
				} else {
					refDto = controller.getAvailableExternalReference(type, roleplayerTypeId, anchor);
					blankTest("type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - Value = " + refDto.getReference()
							+ "   reserved=" + refDto.isReserved()
							+ "   reservedTime=" + refDto.getReservedUntilTime());
					
					logger.info("after (call passed) - type="+type
							+",role=" + roleplayerTypeId
							+",anchor=" + anchor
							+ "   - Value = " + refDto.getReference()
							+ "   reserved=" + refDto.isReserved()
							+ "   reservedTime=" + refDto.getReservedUntilTime());
				}
				
				
			} catch (NamingException | DataNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				blankTest("Got an error - " + e.getMessage());
			}
			
			
		}else if (parms.get("aqc").isNull()==false) {
			 Long l = parms.get("aqc").toLongObject();
			 String codes = "";
			 try {

				for (EffectiveAQCDTO d : ServiceLocator.lookupService(
						IAgreementManagement.class).getAQCodesForAgreement(l)) {
					System.out.println(l + " - " + d.getAqcType() + " - " + d.getValue());
					codes+=d.getAqcType() + " - " + d.getValue() + "  ,";
				}
			} catch (DataNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			blankTest("AQC codes :" + codes);
		}else if (parms.get("aqc2").isNull()==false) {
			 Long l = parms.get("aqc").toLongObject();
			 String codes = "";
			 try {
				 AdvisorQualityCodeDTO dto = ServiceLocator.lookupService(
							IAgreementManagement.class).getAdvisorQualityCodeDTOForAgreement(l);
				for (EffectiveAQCDTO d : dto.getEffectiveAQCValues()) {
					System.out.println("EFF " + l + " - " + d.getAqcType() + " - " + d.getValue());
					codes+=d.getAqcType() + " - " + d.getValue() + "  ,";
				}
				for (AQCDetailsWithTypeDTO d : dto.getAqcDetailsWithTypeDTO()) {
					System.out.println("EFF " + l + " - " + d.getAqcType() + " - " + d.getCalculatedAQCDTO());
					codes+=d.getAqcType() + " - " + d.getManualAQCDTOs() + "  ,";
				}
				
			} catch (DataNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			blankTest("AQC codes :" + codes);
		}else if (parms.get("mailto").isNull()==false) {
		
			
			String mailTo = parms.get("mailto").toString();
//			mailTo = "jean.bodemer@liberty.co.za";
			String mailFrom = parms.get("mailfrom").toString();
			if (mailFrom!=null) {
				mailFrom=parms.get("mailfrom").toString();
			} else {
				mailFrom="sweta.menon@liberty.co.za";
			}
			System.out.println("From=" + mailFrom + "    mailTo=" + mailTo);
			try {
				new MailHelper().sendMail(mailTo,mailFrom, "Coffee", "Coffee on me guys, whatever you want",null);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			blankTest("Send mail to " + mailTo);
			
			
		} else if (parms.get("privilege").isNull()==false) {
			Logger.getLogger("org.apache.openjpa").setLevel(Level.TRACE);
			System.out.println("Testing agreement priviliges = " + parms.get("privilege"));
			blankTest("Privilege = " + parms.get("privilege"));
			try {
				Logger.getLogger(AgreementPrivilegeManagement.class).setLevel(Level.DEBUG);
				IAgreementPrivilegeManagement bean = ServiceLocator.lookupService(IAgreementPrivilegeManagement.class);
				bean.updatePrivilegesWithSeculinkData(parms.get("privilege").toString(), "DONE");
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (parms.get("privilege2").isNull()==false) {
			System.out.println("Testing agreement priviliges2 = " + parms.get("privilege2").toString());
			blankTest("Privilege = " + parms.get("privilege").toString());
			try {
//				154484
				Logger.getLogger("org.apache.openjpa").setLevel(Level.TRACE);
				List<PartyProfileExplicitAgreementsEntity> list = ServiceLocator.lookupService(IExplicitAgreementEntityManager.class)
						.findAllPartyProfileExplicitAgreementsWithPartyAndAgreement(154484L, 1L);
				ServiceLocator.lookupService(IExplicitAgreementEntityManager.class).detachPartyProfileExplicitAgreementList(list);
				
				// first get link objects as these are needed
				PartyProfileEntity partyProfile = ServiceLocator.lookupService(IPartyProfileEntityManager.class)
						.findPartyProfileWithPartyOid(154484);
				PartyProfileExplicitAgreementsEntity entity = new PartyProfileExplicitAgreementsEntity();
				entity.setPartyProfileEntity(partyProfile);
				entity.setCreatedBy("SECULINK");
				entity.setAgreementOid(1);				
				
				Logger.getLogger(AgreementPrivilegeManagement.class).setLevel(Level.DEBUG);
				ServiceLocator.lookupService(
						IExplicitAgreementEntityManager.class).addPartyProfileExplicitAgreement(entity, "JEAN");

			} catch (NamingException e) {
				e.printStackTrace();
			} catch (DataNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if (parms.get("privilege").isNull()==false) {
			System.out.println("Testing agreement priviliges = " + parms.get("privilege").toString());
			blankTest("Privilege = " + parms.get("privilege").toString());
			try {
				Logger.getLogger(AgreementPrivilegeManagement.class).setLevel(Level.DEBUG);
				IAgreementPrivilegeManagement bean = ServiceLocator.lookupService(IAgreementPrivilegeManagement.class);
				bean.updatePrivilegesWithSeculinkData(parms.get("privilege").toString(), "DONE");
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (parms.get("request").isNull()==false) {
			System.out.println("request test");
			try {
				IRequestManagement service = ServiceLocator.lookupService(IRequestManagement.class);
				BigDecimal tmpAmount = null;
				if (parms.get("request").toString()!=null) {
					try {
						tmpAmount = new BigDecimal(parms.get("request").toString().trim());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (tmpAmount == null) {
					tmpAmount = new BigDecimal("100000.00");
				}
				CurrencyAmount amount = CurrencyAmountUtil.create(tmpAmount);
				service.testCreateDPE(((SRSAuthWebSession)this.getSession()).getSessionUser(),
						440719L, amount);
				blankTest("Completed request.evaluate");
				
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				blankTest("Error : " + e.getMessage());
			} catch (RuleLimitArgumentRuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				blankTest("Validation failed : " + e.getMessage());
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				blankTest("Error : " + e.getMessage());
			}
		} else if (parms.get("geocode").isNull()==false) {
			System.out.println("GEOCODE TEST");
			try {
				IGeocodingServiceManagement service = ServiceLocator.lookupService(IGeocodingServiceManagement.class);
				service.retrieveGeoLocation(new PhysicalAddressDTO());
				System.out.println("JEAN - test4");
				initialiseTest4();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else {
			System.out.println("No match");
			blankTest("No match");
//		if (parms.get("hierarchy")!=null && parms.get("requestKind")!=null) {
//			long hierarchy = parms.getLong("hierarchy");
//			long requestKind = parms.getLong("requestKind");
//			
//			GuiRequestImageTypeEntity type = GuiRequestImageTypeEntity.CurrentImage;
//			
//			if (parms.get("image")!=null) {
//				if (parms.getString("image").equalsIgnoreCase("before")) {
//					type = GuiRequestImageTypeEntity.BeforeImage;
//					System.out.println("Retrieving before image");
//				}
//			}
//			System.out.println("Hierarchy="+hierarchy+" ,requestKind="+requestKind
//					+ " ,imageType="+type);
//			testHierarchyGuiRequest(hierarchy, requestKind, type);
//		} else if (parms.get("party")!=null && parms.get("requestKind")!=null) {
//			long partyRequestKey = parms.getLong("party");
//			long requestKind = parms.getLong("requestKind");
//			
//			GuiRequestImageTypeEntity type = GuiRequestImageTypeEntity.CurrentImage;
//			
//			if (parms.get("image")!=null) {
//				if (parms.getString("image").equalsIgnoreCase("before")) {
//					type = GuiRequestImageTypeEntity.BeforeImage;
//					System.out.println("Retrieving before image");
//				}
//			}
//			System.out.println("partyRequestKey="+partyRequestKey+" ,requestKind="+requestKind
//					+ " ,imageType="+type);
//			testPartyGuiRequest(partyRequestKey, requestKind, type);
//		}else {
//			System.out.println("No parameters defined");
//		
//			throw new IllegalArgumentException("Correct parameters were not received");
//		}
		
//		initialiseTest3();
		
			
		}
		
		
		
	}	
	private void blankTest(String message) {
		// TODO Auto-generated method stub
		Form form = new Form("form");
		add(form);
		form.setOutputMarkupId(true);
		Panel panel = HelperPanel.getInstance("panel1", new Label("value", message));
		form.add(panel);
		form.add(new EmptyPanel("panel2"));
		form.add(new EmptyPanel("panel3"));
		this.add(form);
	}
	protected void initialiseTest1() {
		Form form = new Form("form") {
			 @Override
		     protected void onSubmit() {
				 System.out.println("form.onSubmit - Start");
				 System.out.println("form.onSubmit - End");
			 }
		};
	
		Button but = new AjaxButton("value", form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("AjaxButton.onsubmit - End");
			}


		};;
		
		
//		Button but = new Button("value") {
//			
//			@Override
//			protected void onComponentTag(ComponentTag tag) {
//				super.onComponentTag(tag);
//				tag.getAttributes().put("type", "submit");
//			}
//			
//			@Override
//			public void onSubmit() {
//				
//				// Download the file
//				System.out.println("button.onSubmit - before sleep");
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
////				final String fn = WicketURLEncoder.QUERY_INSTANCE.encode(fileName);
////				
////				IResourceStream resourceStream = new FileResourceStream(
////					new org.apache.wicket.util.file.File(file));
////				
////				getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) 	{
////					@Override
////					public String getFileName() {
////						return fn;
////					}
////
////					@Override
////					public void respond(RequestCycle requestCycle) 			{
////						super.respond(requestCycle);
////					}
////				});
//				
//				System.out.println("button.onSubmit - after sleep - Done");
//			}
//		};
		but.setOutputMarkupId(true);
		but.add(new AjaxEventBehavior("onsubmit") {
			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				System.out.println("button.onclick - Start");
			
				System.out.println("button.onclick - End");
				
			}
		});
		
		form.setOutputMarkupId(true);
		
//		but.add(new AjaxFormSubmitBehavior(form, "click") {
//		    @Override
//		    protected void onEvent(AjaxRequestTarget target) {
//		    	target.prependJavascript("overlay(true);");
//		        /* This will do actual validation and submit, do not remove it */
//		        super.onEvent(target);
//		        /* With Ajax submit feedback must be filled manually */
//
//		    }
//
//			@Override
//			protected void onError(AjaxRequestTarget arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			protected void onSubmit(AjaxRequestTarget arg0) {
//				// TODO Auto-generated method stub
//				System.out.println("formsubmit.submit - Done");
//			}
//		});
		
		form.add(HelperPanel.getInstance("panel", but));
		this.add(form);
	}
	
	
	protected void initialiseTest2() {
		final AjaxDownload download = new AjaxDownload() {

			@Override
			protected IResourceStream getResourceStream() {
				
				return new FileResourceStream(
					new org.apache.wicket.util.file.File(new File("C:\\tst.pdf")));
				
				
			}

			@Override
			protected String getFileName() {
				// TODO Auto-generated method stub
				return "Test.pdf";
			}
		};
	
		Form form = new Form("form") {
			 @Override
		     protected void onSubmit() {
				 System.out.println("form.onSubmit - Start");
				 System.out.println("form.onSubmit - End");
			 }
		};
	
		Button but = new AjaxButton("value", form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("AjaxButton.onsubmit - End");
			}

		};;
		
		

		but.setOutputMarkupId(true);
		but.add(new AjaxEventBehavior("click") {
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				System.out.println("button.onclick - Start");
				download.initiate(target);
				System.out.println("button.onclick - End");
				
			}
		});
		
		form.setOutputMarkupId(true);
		form.add(HelperPanel.getInstance("panel", but));
		this.add(form);
		
//		item.add(download);
//		�
//		item.add(new AjaxLink<Void>("link") {
//		����@Override
//		����public void onClick(AjaxRequestTarget target)
//		����{
//		��������// do whatever with the target, e.g. refresh components
//		��������target.add(...);
//		�
//		��������// finally initiate the download
//		��������download.initiate(target);
//		����}
//		});
	}
	
	protected void initialiseTest3() {
		final AjaxDownload download = new AjaxDownload() {

			@Override
			protected IResourceStream getResourceStream() {
				
				return new FileResourceStream(
					new org.apache.wicket.util.file.File(new File("C:\\tst.pdf")));
				
				
			}

			@Override
			protected String getFileName() {
				// TODO Auto-generated method stub
				return "Test.pdf";
			}
		};
	
		Form form = new Form("form") {
			 @Override
		     protected void onSubmit() {
				 System.out.println("form.onSubmit - Start");
				 System.out.println("form.onSubmit - End");
			 }
		};
		AjaxLink but = new AjaxLink<Void>("value") {
		    @Override
		    public void onClick(AjaxRequestTarget target) {
		        // do whatever with the target, e.g. refresh components
		        System.out.println("link.onclick - start");
		 
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				  System.out.println("link.onclick - after sleep");
		        
		        // finally initiate the download
		        download.initiate(target);
		        
		        System.out.println("link.onclick - after initiate");
		    }

			
		};
		
		
		
		
		

		but.setOutputMarkupId(true);
//		AjaxEventBehavior behave = new AjaxEventBehavior("click") {
//			
//			@Override
//			protected void onEvent(AjaxRequestTarget target) {
//				System.out.println("button.onclick - Start");
//				download.initiate(target);
//				System.out.println("button.onclick - End");
//				
//			}
//		};
//		form.add(download);
		form.setOutputMarkupId(true);
		Panel panel = HelperPanel.getInstance("panel1", but);
		panel.add(download);
		form.add(panel);
		form.add(new EmptyPanel("panel2"));
		form.add(new EmptyPanel("panel3"));
		this.add(form);
		
//		item.add(download);
//		�
//		item.add(new AjaxLink<Void>("link") {
//		����@Override
//		����public void onClick(AjaxRequestTarget target)
//		����{
//		��������// do whatever with the target, e.g. refresh components
//		��������target.add(...);
//		�
//		��������// finally initiate the download
//		��������download.initiate(target);
//		����}
//		});
	}
	
	protected void initialiseTest4() {
		Form form = new Form("form") {
			 @Override
		     protected void onSubmit() {
				 System.out.println("form.onSubmit - Start");
				 System.out.println("form.onSubmit - End");
			 }
		};
	
		Button but = new AjaxButton("value", form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				try {
					System.out.println("Queue depth:=" + ServiceLocator.lookupService(ITaxGuiController.class).getCatchQueueCount());
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("AjaxButton.onsubmit - End");
			}

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//
//					public CharSequence decorateScript(CharSequence script) {
//						return "overlay(true);" + script;
//					}
//				};
//			}
		};;
		

		but.setOutputMarkupId(true);
		but.add(new AjaxEventBehavior("onsubmit") {
			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				System.out.println("button.onclick - Start");
				try {
					System.out.println("Queue depth:=" + ServiceLocator.lookupService(ITaxGuiController.class).getCatchQueueCount());
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("button.onclick - End");
				
			}
		});
		
		form.setOutputMarkupId(true);
		
		form.add(HelperPanel.getInstance("panel1", but));
		form.add(new EmptyPanel("panel2"));
		form.add(new EmptyPanel("panel3"));
		this.add(form);
	}
	
	@Override
	protected boolean isCheckAuthentication() {
		/* Disable authentication as we are logging on */
		return true;
	}
	
	@Override
	public String getPageName() {
		return "General Test";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
}


