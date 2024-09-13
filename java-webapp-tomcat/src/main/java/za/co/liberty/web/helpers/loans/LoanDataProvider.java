package za.co.liberty.web.helpers.loans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.loan.InterestRateType;
import za.co.liberty.dto.loan.LoanBaseDTO;
import za.co.liberty.dto.loan.LoanDTO;
import za.co.liberty.dto.loan.LoanQuoteDTO;

/**
 * Data provider for Loans.  
 * 
 * <p>Note that this is a dummy class that should only be 
 * used for testing and that changes made to objects retrieved
 * from here will persist in the list.</p>
 * 
 * @author JZB0608 - 10 Apr 2008
 *
 */
public class LoanDataProvider {
	
	private static LoanDataProvider self;
	private ArrayList<LoanQuoteDTO> loanQuoteList;
	private ArrayList<LoanDTO> loanList;
	
	/**
	 * Defined as private to enforce singleton pattern
	 *
	 */
	private LoanDataProvider() {
		loanQuoteList = initQuoteList();
		loanList = initLoanList();
	}
	
	/**
	 * Initialises the loan quote list
	 * 
	 * @return
	 */
	private ArrayList<LoanQuoteDTO> initQuoteList() {
		ArrayList<LoanQuoteDTO> list = new ArrayList();
		list.add((LoanQuoteDTO) createQuote(new LoanQuoteDTO(), 612, 1, 100000, 10, InterestRateType.FIXED, 12, 8791.59));
		list.add((LoanQuoteDTO) createQuote(new LoanQuoteDTO(),612, 2, 50000, -2, InterestRateType.PRIME_LINKED, 12, 4454.14));
		list.add((LoanQuoteDTO) createQuote(new LoanQuoteDTO(),612, 3, 40000, 0, InterestRateType.REPO_RATE, 24, 1845.80));
		list.add((LoanQuoteDTO) createQuote(new LoanQuoteDTO(),163000, 4, 60000, 10, InterestRateType.FIXED, 24, 2768.70));
		list.add((LoanQuoteDTO) createQuote(new LoanQuoteDTO(),163000, 5, 100000, 1, InterestRateType.REPO_RATE, 48, 2584.55));
		return list;
	}
	
	/**
	 * Initialises the loan list
	 * 
	 * @return
	 */
	private ArrayList<LoanDTO> initLoanList() {
		ArrayList<LoanDTO> list = new ArrayList();
		list.add((LoanDTO) createQuote(new LoanDTO(), 612, 1, 100000, 10, InterestRateType.FIXED, 12, 8791.59));
		LoanDTO obj = (LoanDTO) createQuote(new LoanDTO(),612, 2, 50000, -2, InterestRateType.PRIME_LINKED, 12, 4454.14);
		obj.setLinkedQuoteOid(1);
		list.add(obj);
		return list;
	}
	
	/**
	 * Helper method that is used to create a LoanQuote DTO\
	 * 
	 * @param agreementNo
	 * @param oid
	 * @param loanAmount
	 * @param interestDiff
	 * @param interestType
	 * @param term
	 * @param paymentAmount
	 * @return
	 */
	public LoanBaseDTO createQuote(LoanBaseDTO obj, int agreementNo, int oid, 
			double loanAmount, double interestDiff,
			InterestRateType interestType, 
			int term, double paymentAmount)	{
		
		obj.setAgreementNo(agreementNo);
		obj.setOid(oid);
		obj.setLoanAmount(new BigDecimal(loanAmount));
		obj.setInterestDifferential(new BigDecimal(interestDiff));
		
		obj.setInterestRateDetermination(interestType);
		obj.setPaymentAmount(new BigDecimal(paymentAmount));
		obj.setTermMonths(term);
		
		obj.calcInterestRateTotal();
		return obj;
	}


	/**
	 * Get an instance of this object
	 * 
	 * @return
	 */
	public static LoanDataProvider getInstance() {
		if (self == null) {
			self = new LoanDataProvider();
		}
		return self;
	}

	/**
	 * Returns the list of loan quotes
	 * 
	 * @return
	 */
	public ArrayList<LoanQuoteDTO> getLoanQuoteList() {
		return loanQuoteList;
	}
	
	/**
	 * Returns the list of loan quotes
	 * 
	 * @return
	 */
	public ArrayList<LoanDTO> getLoanList() {
		return loanList;
	}
	
	/**
	 * Get quotes for the specified agreement
	 * 
	 * @param agreementNo
	 * @return
	 */
	public List<LoanQuoteDTO> getQuotesForAgreement(long agreementNo) {
		List<LoanQuoteDTO> result = new ArrayList<LoanQuoteDTO>();
		LoanQuoteDTO loanDTO = null;
		
		if (agreementNo <= 0) {
			return result;
		}
		

		for (LoanQuoteDTO bean : loanQuoteList) {
			if (loanDTO.getAgreementNo().equals(agreementNo)) {
				result.add(loanDTO);
			}			
		}		

		return result;
	}	
}
